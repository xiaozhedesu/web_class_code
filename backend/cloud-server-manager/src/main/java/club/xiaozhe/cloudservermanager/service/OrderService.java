package club.xiaozhe.cloudservermanager.service;

import club.xiaozhe.cloudservermanager.dto.CreateOrderRequest;
import club.xiaozhe.cloudservermanager.dto.OrderResponse;
import club.xiaozhe.cloudservermanager.entity.Order;
import club.xiaozhe.cloudservermanager.entity.Server;
import club.xiaozhe.cloudservermanager.entity.User;
import club.xiaozhe.cloudservermanager.exception.BusinessException;
import club.xiaozhe.cloudservermanager.exception.ErrorCode;
import club.xiaozhe.cloudservermanager.repository.OrderRepository;
import club.xiaozhe.cloudservermanager.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ServerService serverService;
    private final SecurityUtil securityUtil;

    /* ----- tools ----- */

    /**
     * 根据id获取订单信息
     *
     * @param id 订单id
     * @return 订单实体对象
     */
    Order getOrderById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    /* ----- apis ----- */

    /**
     * 用户创建租赁订单
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Integer serverId = request.serverId();
        Integer months = request.months();

        Server server = serverService.findServerById(serverId);

        BigDecimal totalPrice = server.getPricePerMonth().multiply(BigDecimal.valueOf(months));

        User user = securityUtil.getCurrentUser();
        Order order = new Order();
        order.setUserId(user.getId());
        order.setServerId(serverId);
        order.setMonths(months);
        order.setTotalPrice(totalPrice);
        order.setStatus(Order.Status.PENDING);

        return OrderResponse.from(orderRepository.save(order), user);
    }

    /**
     * 用户查看自己的订单
     */
    public List<OrderResponse> listMyOrders() {
        User user = securityUtil.getCurrentUser();
        List<Order> orders = orderRepository.findByUserId(user.getId());
        return orders.stream()
                .map(order -> OrderResponse.from(order, user))
                .toList();
    }

    /**
     * 管理员查看所有订单
     */
    public List<OrderResponse> listAllOrders() {
        List<Order> orders = orderRepository.findAll();

        Set<Integer> userIds = orders.stream()
                .map(Order::getUserId)
                .collect(Collectors.toSet());
        Map<Integer, User> userMap = userService.findUserListById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return orders.stream()
                .map(order -> OrderResponse.from(order, userMap.get(order.getUserId())))
                .toList();
    }

    /**
     * 管理员修改订单状态
     */
    @Transactional
    public OrderResponse updateOrderStatus(Integer id, Order.Status status) {
        Order order = getOrderById(id);
        order.setStatus(status);

        User user = userService.findUserById(order.getUserId());
        return OrderResponse.from(orderRepository.save(order), user);
    }

    /**
     * 用户修改自己订单状态（只能取消或支付）
     */
    @Transactional
    public OrderResponse updateMyOrderStatus(Integer id, Order.Status status) {
        // 限制修改状态
        // 这个函数本来就别扭，生产代码不会有这种走向来的吧，但是我没有写状态机逻辑
        Set<Order.Status> allowed = Set.of(Order.Status.CANCELLED, Order.Status.PAID);
        if (!allowed.contains(status)) {
            throw new BusinessException(ErrorCode.STATUS_UNDEFINED, "用户只允许修改为PAID|CANCELLED");
        }

        Order order = getOrderById(id);
        order.setStatus(status);

        User user = securityUtil.getCurrentUser();
        if (!order.getUserId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "无权查看该订单");
        }

        return OrderResponse.from(orderRepository.save(order), user);
    }

    /**
     * 用户查看单个订单
     *
     * @apiNote GET /api/user/orders/{id}
     */
    public OrderResponse getMyOrder(Integer id) {
        User user = securityUtil.getCurrentUser();

        Order order = getOrderById(id);
        if (!order.getUserId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "无权查看该订单");
        }

        return OrderResponse.from(order, user);
    }
}
