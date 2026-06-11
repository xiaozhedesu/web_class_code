package club.xiaozhe.cloudservermanager.dto;

import club.xiaozhe.cloudservermanager.entity.Order;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull(message = "状态码不能为空！")
        Order.Status status
) {
}
