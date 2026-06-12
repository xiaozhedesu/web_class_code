package club.xiaozhe.cloudservermanager.controller;

import club.xiaozhe.cloudservermanager.dto.ApiResponse;
import club.xiaozhe.cloudservermanager.dto.CreateOrderRequest;
import club.xiaozhe.cloudservermanager.dto.OrderResponse;
import club.xiaozhe.cloudservermanager.dto.UpdateStatusRequest;
import club.xiaozhe.cloudservermanager.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 用户创建租赁订单
     *
     * @apiNote POST /api/user/orders
     */
    @PostMapping("/user/orders")
    public ApiResponse<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        return ApiResponse.success(orderService.createOrder(request));
    }

    /**
     * 用户查看自己的订单
     *
     * @apiNote GET /api/user/orders
     */
    @GetMapping("/user/orders")
    public ApiResponse<List<OrderResponse>> listMyOrders() {
        return ApiResponse.success(orderService.listMyOrders());
    }

    /**
     * 管理员查看所有订单
     *
     * @apiNote GET /api/admin/orders
     */
    @GetMapping("/admin/orders")
    public ApiResponse<List<OrderResponse>> listAllOrders() {
        return ApiResponse.success(orderService.listAllOrders());
    }

    /**
     * 管理员修改订单状态
     *
     * @apiNote PUT /api/admin/orders/{id}/status
     */
    @PutMapping("/admin/orders/{id}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateStatusRequest request
    ) {
        return ApiResponse.success(orderService.updateOrderStatus(id, request.status()));
    }

    /**
     * 用户查看单个订单
     *
     * @apiNote GET /api/user/orders/{id}
     */
    @GetMapping("/user/orders/{id}")
    public ApiResponse<OrderResponse> getMyOrder(@PathVariable Integer id) {
        return ApiResponse.success(orderService.getMyOrder(id));
    }

    /**
     * 用户修改自己订单状态（只能取消或支付）
     *
     * @apiNote PUT /api/user/orders/{id}/status
     */
    @PutMapping("/user/orders/{id}/status")
    public ApiResponse<OrderResponse> updateMyOrderStatus(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateStatusRequest request) {
        return ApiResponse.success(orderService.updateMyOrderStatus(id, request.status()));
    }
}
