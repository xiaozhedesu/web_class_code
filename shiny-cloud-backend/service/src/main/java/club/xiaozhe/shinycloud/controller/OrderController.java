package club.xiaozhe.shinycloud.controller;

import club.xiaozhe.shinycloud.dto.Result;
import club.xiaozhe.shinycloud.dto.CreateOrderRequest;
import club.xiaozhe.shinycloud.dto.OrderResponse;
import club.xiaozhe.shinycloud.dto.UpdateStatusRequest;
import club.xiaozhe.shinycloud.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    /**
     * 用户创建租赁订单
     *
     * @apiNote POST /api/user/orders
     */
    @PostMapping("/user/orders")
    public Result<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        return Result.success(orderService.createOrder(request));
    }

    /**
     * 用户查看自己的订单
     *
     * @apiNote GET /api/user/orders
     */
    @GetMapping("/user/orders")
    public Result<List<OrderResponse>> listMyOrders() {
        return Result.success(orderService.listMyOrders());
    }

    /**
     * 管理员查看所有订单
     *
     * @apiNote GET /api/admin/orders
     */
    @GetMapping("/admin/orders")
    public Result<List<OrderResponse>> listAllOrders() {
        return Result.success(orderService.listAllOrders());
    }

    /**
     * 管理员修改订单状态
     *
     * @apiNote PUT /api/admin/orders/{id}/status
     */
    @PutMapping("/admin/orders/{id}/status")
    public Result<OrderResponse> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateStatusRequest request
    ) {
        return Result.success(orderService.updateOrderStatus(id, request.status()));
    }

    /**
     * 用户查看单个订单
     *
     * @apiNote GET /api/user/orders/{id}
     */
    @GetMapping("/user/orders/{id}")
    public Result<OrderResponse> getMyOrder(@PathVariable Integer id) {
        return Result.success(orderService.getMyOrder(id));
    }

    /**
     * 用户修改自己订单状态（只能取消或支付）
     *
     * @apiNote PUT /api/user/orders/{id}/status
     */
    @PutMapping("/user/orders/{id}/status")
    public Result<OrderResponse> updateMyOrderStatus(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateStatusRequest request) {
        return Result.success(orderService.updateMyOrderStatus(id, request.status()));
    }
}
