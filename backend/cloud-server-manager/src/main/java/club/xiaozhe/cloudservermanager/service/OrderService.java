package club.xiaozhe.cloudservermanager.service;

import club.xiaozhe.cloudservermanager.dto.CreateOrderRequest;
import club.xiaozhe.cloudservermanager.dto.OrderResponse;
import club.xiaozhe.cloudservermanager.entity.Order;
import club.xiaozhe.cloudservermanager.entity.Server;
import club.xiaozhe.cloudservermanager.entity.User;
import club.xiaozhe.cloudservermanager.exception.AuthException;
import club.xiaozhe.cloudservermanager.exception.UserNotFoundException;
import club.xiaozhe.cloudservermanager.repository.OrderRepository;
import club.xiaozhe.cloudservermanager.repository.ServerRepository;
import club.xiaozhe.cloudservermanager.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ServerRepository serverRepository;

    public OrderService(UserRepository userRepository, OrderRepository orderRepository, ServerRepository serverRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.serverRepository = serverRepository;
    }

    /**
     * 获取当前用户对象，如果为空则抛出异常。
     *
     * @return User
     */
    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        if (username == null) {
            throw new UserNotFoundException();
        }
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(UserNotFoundException::new);
    }

    private Order getOrderById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
    }

    private User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    private Server getServerById(Integer id) {
        return serverRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("服务器套餐不存在"));
    }

    /**
     * 用户创建租赁订单，自动计算总价
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Integer serverId = request.serverId();
        Integer months = request.months();

        Server server = getServerById(serverId);

        BigDecimal totalPrice = server.getPricePerMonth().multiply(BigDecimal.valueOf(months));

        User user = currentUser();
        Order order = new Order();
        order.setUserId(user.getId());
        order.setServerId(serverId);
        order.setMonths(months);
        order.setTotalPrice(totalPrice);
        order.setStatus(Order.PENDING);

        return OrderResponse.from(orderRepository.save(order), user);
    }

    /**
     * 查询用户的订单列表
     */
    public List<OrderResponse> listMyOrders() {
        User user = currentUser();
        List<Order> orders = orderRepository.findByUserId(user.getId());
        return orders.stream()
                .map(order -> OrderResponse.from(order, user))
                .toList();
    }

    /**
     * 查询所有订单（管理员）
     */
    public List<OrderResponse> listAllOrders() {
        List<Order> orders = orderRepository.findAll();

        Set<Integer> userIds = orders.stream()
                .map(Order::getUserId)
                .collect(Collectors.toSet());
        Map<Integer, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return orders.stream()
                .map(order -> OrderResponse.from(order, userMap.get(order.getUserId())))
                .toList();
    }

    /**
     * 管理员修改指定订单状态
     */
    @Transactional
    public OrderResponse updateStatusAsAdmin(Integer id, String status) {
        Order order = getOrderById(id);
        order.setStatus(status);

        User user = getUserById(order.getUserId());
        return OrderResponse.from(orderRepository.save(order), user);
    }

    /**
     * 用户修改指定订单状态
     * @param id 订单id
     * @param status 状态码
     * @return OrderResponse
     */
    @Transactional
    public OrderResponse updateStatusAsUser(Integer id, String status) {
        // 限制修改状态
        // 这个函数本来就别扭，生产代码不会有这种走向来的吧，但是我没有写状态机逻辑
        Set<String> allowed = Set.of(Order.CANCELLED, Order.PAID);
        if (!allowed.contains(status)) {
            throw new IllegalArgumentException("无效的状态值，用户只允许修改为PAID|CANCELLED");
        }

        Order order = getOrderById(id);
        order.setStatus(status);

        User user = currentUser();
        if (!order.getUserId().equals(user.getId())) {
            throw new AuthException("无权查看该订单");
        }

        return OrderResponse.from(orderRepository.save(order), user);
    }

    /**
     * 用户查看单个订单
     * @param id 订单号
     * @return OrderResponse
     */
    public OrderResponse getMyOrder(Integer id) {
        User user = currentUser();

        Order order = getOrderById(id);
        if (!order.getUserId().equals(user.getId())) {
            throw new AuthException("无权查看该订单");
        }

        return OrderResponse.from(order, user);
    }
}
