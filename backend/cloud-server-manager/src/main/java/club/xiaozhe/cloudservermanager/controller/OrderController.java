package club.xiaozhe.cloudservermanager.controller;

import club.xiaozhe.cloudservermanager.dto.ApiResponse;
import club.xiaozhe.cloudservermanager.dto.CreateOrderRequest;
import club.xiaozhe.cloudservermanager.dto.OrderResponse;
import club.xiaozhe.cloudservermanager.entity.Order;
import club.xiaozhe.cloudservermanager.entity.User;
import club.xiaozhe.cloudservermanager.exception.UserNotFoundException;
import club.xiaozhe.cloudservermanager.repository.UserRepository;
import club.xiaozhe.cloudservermanager.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ApiResponse<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        User user = currentUser();
        if (user == null) {
            throw new UserNotFoundException();
        }

        var order = orderService.createOrder(user.getId(), request.serverId(), request.months());
        return ApiResponse.success(OrderResponse.from(order, user));
    }

    /**
     * 用户查看自己的订单
     * GET /api/user/orders
     */
    @GetMapping("/user/orders")
    public ApiResponse<List<OrderResponse>> listMyOrders() {
        User user = currentUser();
        if (user == null) {
            throw new UserNotFoundException();
        }

        var orders = orderService.findByUserId(user.getId()).stream()
                .map(o -> OrderResponse.from(o, user))
                .toList();
        return ApiResponse.success(orders);
    }

    /**
     * 管理员查看所有订单
     * GET /api/admin/orders
     */
    @GetMapping("/admin/orders")
    public ApiResponse<List<OrderResponse>> listAllOrders() {
        var orders = orderService.listAll();

        var userIds = orders.stream().map(Order::getUserId).collect(Collectors.toSet());
        var userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        var result = orders.stream()
                .map(o -> OrderResponse.from(o, userMap.get(o.getUserId())))
                .toList();
        return ApiResponse.success(result);
    }

    /**
     * 管理员修改订单状态
     * PUT /api/admin/orders/{id}/status
     */
    @PutMapping("/admin/orders/{id}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null) {
            return ApiResponse.error(400, "缺少 status 字段");
        }

        Set<String> allowed = Set.of(Order.PENDING, Order.PAID, Order.CANCELLED, Order.COMPLETED);
        if (!allowed.contains(status)) {
            return ApiResponse.error(400, "无效的状态值，允许：PENDING、PAID、CANCELLED、COMPLETED");
        }

        var order = orderService.updateStatus(id, status);
        User user = userRepository.findById(order.getUserId()).orElse(null);
        return ApiResponse.success(OrderResponse.from(order, user));
    }

    /**
     * 用户查看单个订单
     * GET /api/user/orders/{id}
     */
    @GetMapping("/user/orders/{id}")
    public ApiResponse<OrderResponse> getMyOrder(@PathVariable Integer id) {
        User user = currentUser();
        if (user == null) {
            throw new UserNotFoundException();
        }

        var order = orderService.findById(id);
        if (!order.getUserId().equals(user.getId())) {
            return ApiResponse.error(403, "无权查看该订单");
        }
        return ApiResponse.success(OrderResponse.from(order, user));
    }

    /**
     * 用户修改自己订单状态（只能取消或支付）
     * PUT /api/user/orders/{id}/status
     */
    @PutMapping("/user/orders/{id}/status")
    public ApiResponse<OrderResponse> updateMyOrderStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        User user = currentUser();
        if (user == null) {
            throw new UserNotFoundException();
        }

        String status = body.get("status");
        if (status == null) {
            return ApiResponse.error(400, "缺少 status 字段");
        }

        Set<String> allowed = Set.of(Order.CANCELLED, Order.PAID);
        if (!allowed.contains(status)) {
            return ApiResponse.error(400, "无效的状态值，允许：PAID、CANCELLED");
        }

        var order = orderService.findById(id);
        if (!order.getUserId().equals(user.getId())) {
            return ApiResponse.error(403, "无权操作该订单");
        }
        order = orderService.updateStatus(id, status);
        return ApiResponse.success(OrderResponse.from(order, user));
    }
}
