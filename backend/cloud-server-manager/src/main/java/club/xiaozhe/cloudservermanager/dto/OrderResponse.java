package club.xiaozhe.cloudservermanager.dto;

import club.xiaozhe.cloudservermanager.entity.Order;
import club.xiaozhe.cloudservermanager.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Integer id,
        Integer userId,
        String username,
        String realName,
        Integer serverId,
        Integer months,
        BigDecimal totalPrice,
        String status,
        LocalDateTime orderTime
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(), order.getUserId(), null, null, order.getServerId(),
                order.getMonths(), order.getTotalPrice(), order.getStatus(),
                order.getOrderTime()
        );
    }

    public static OrderResponse from(Order order, User user) {
        return new OrderResponse(
                order.getId(), order.getUserId(), user.getUsername(), user.getRealName(),
                order.getServerId(), order.getMonths(), order.getTotalPrice(),
                order.getStatus(), order.getOrderTime()
        );
    }
}
