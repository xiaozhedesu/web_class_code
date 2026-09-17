package club.xiaozhe.shinycloud.dto;

import club.xiaozhe.shinycloud.entity.Order;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull(message = "状态码不能为空！")
        Order.Status status
) {
}
