package club.xiaozhe.cloudservermanager.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateStatusRequest(
        @NotNull(message = "状态码不能为空！")
        @Pattern(regexp = "PENDING|PAID|CANCELLED|COMPLETED",
                message = "状态只支持四种值：PENDING|PAID|CANCELLED|COMPLETED")
        String status
) {
}
