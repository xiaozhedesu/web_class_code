package club.xiaozhe.cloudservermanager.service;

import club.xiaozhe.cloudservermanager.entity.Order;
import club.xiaozhe.cloudservermanager.entity.Server;
import club.xiaozhe.cloudservermanager.repository.OrderRepository;
import club.xiaozhe.cloudservermanager.repository.ServerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ServerRepository serverRepository;

    public OrderService(OrderRepository orderRepository, ServerRepository serverRepository) {
        this.orderRepository = orderRepository;
        this.serverRepository = serverRepository;
    }

    /**
     * 用户创建租赁订单，自动计算总价
     */
    @Transactional
    public Order createOrder(Integer userId, Integer serverId, Integer months) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("服务器套餐不存在"));

        BigDecimal totalPrice = server.getPricePerMonth().multiply(BigDecimal.valueOf(months));

        Order order = new Order();
        order.setUserId(userId);
        order.setServerId(serverId);
        order.setMonths(months);
        order.setTotalPrice(totalPrice);
        order.setStatus(Order.PENDING);
        return orderRepository.save(order);
    }

    /**
     * 查询用户的订单列表
     */
    public List<Order> findByUserId(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    /**
     * 查询所有订单（管理员）
     */
    public List<Order> listAll() {
        return orderRepository.findAll();
    }

    /**
     * 修改订单状态
     */
    @Transactional
    public Order updateStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
