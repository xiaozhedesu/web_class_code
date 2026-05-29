package club.xiaozhe.cloudservermanager.controller;

import club.xiaozhe.cloudservermanager.dto.CreateOrderRequest;
import club.xiaozhe.cloudservermanager.dto.OrderResponse;
import club.xiaozhe.cloudservermanager.entity.Order;
import club.xiaozhe.cloudservermanager.entity.User;
import club.xiaozhe.cloudservermanager.exception.UserNotFoundException;
import club.xiaozhe.cloudservermanager.repository.UserRepository;
import club.xiaozhe.cloudservermanager.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName()).orElse(null);
    }

    /**
     * 用户创建租赁订单
     * POST /api/user/orders
     */
    @PostMapping("/user/orders")
    public ResponseEntity<?> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        User user = currentUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "用户不存在"));
        }

        try {
            var order = orderService.createOrder(user.getId(), request.serverId(), request.months());
            return ResponseEntity.ok(OrderResponse.from(order, user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 用户查看自己的订单
     * GET /api/user/orders
     */
    @GetMapping("/user/orders")
    public ResponseEntity<?> listMyOrders() {
        User user = currentUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "用户不存在"));
        }

        var orders = orderService.findByUserId(user.getId()).stream()
                .map(o -> OrderResponse.from(o, user))
                .toList();
        return ResponseEntity.ok(orders);
    }

    /**
     * 管理员查看所有订单
     * GET /api/admin/orders
     */
    @GetMapping("/admin/orders")
    public ResponseEntity<?> listAllOrders() {
        var orders = orderService.listAll();

        // 批量查询订单关联的用户
        var userIds = orders.stream().map(Order::getUserId).collect(Collectors.toSet());
        var userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        var result = orders.stream()
                .map(o -> OrderResponse.from(o, userMap.get(o.getUserId())))
                .toList();
        return ResponseEntity.ok(result);
    }

    /**
     * 管理员修改订单状态
     * PUT /api/admin/orders/{id}/status
     */
    @PutMapping("/admin/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "缺少 status 字段"));
        }

        Set<String> allowed = Set.of(Order.PENDING, Order.PAID, Order.CANCELLED, Order.COMPLETED);
        if (!allowed.contains(status)) {
            return ResponseEntity.badRequest().body(Map.of("message", "无效的状态值，允许：PENDING、PAID、CANCELLED、COMPLETED"));
        }

        try {
            var order = orderService.updateStatus(id, status);
            User user = userRepository.findById(order.getUserId()).orElse(null);
            return ResponseEntity.ok(OrderResponse.from(order, user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 用户查看单个订单
     * GET /api/user/orders/{id}
     */
    @GetMapping("/user/orders/{id}")
    public ResponseEntity<?> getMyOrder(@PathVariable Integer id) {
        User user = currentUser();
        if (user == null) {
            throw new UserNotFoundException();
        }

        try {
            var order = orderService.findById(id);
            if (!order.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("message", "无权查看该订单"));
            }
            return ResponseEntity.ok(OrderResponse.from(order, user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 用户修改自己订单状态（只能取消或支付）
     * PUT /api/user/orders/{id}/status
     */
    @PutMapping("/user/orders/{id}/status")
    public ResponseEntity<?> updateMyOrderStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        User user = currentUser();
        if (user == null) {
            throw new UserNotFoundException();
        }

        String status = body.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "缺少 status 字段"));
        }

        Set<String> allowed = Set.of(Order.CANCELLED, Order.PAID);
        if (!allowed.contains(status)) {
            return ResponseEntity.badRequest().body(Map.of("message", "无效的状态值，允许：PAID、CANCELLED"));
        }

        try {
            var order = orderService.findById(id);
            if (!order.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("message", "无权操作该订单"));
            }
            order = orderService.updateStatus(id, status);
            return ResponseEntity.ok(OrderResponse.from(order, user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
