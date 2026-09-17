package club.xiaozhe.cloudservermanager.dto;

import club.xiaozhe.cloudservermanager.entity.Server;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ServerRequest(
        @NotNull
        String model,
        @Pattern(regexp = "[1-9][0-9]*核", message = "CPU信息格式错误！正确示例：8核")
        String cpu,
        @Pattern(regexp = "[1-9][0-9]*[GT]B" , message = "内存信息格式错误！正确示例：8GB")
        String ram,
        @Pattern(regexp = "[1-9][0-9]*[GT]B (SSD|HDD)", message = "磁盘信息格式错误！正确示例：80GB SSD")
        String disk,
        @Positive
        BigDecimal pricePerMonth,
        Boolean isAvailable
) {
    public Server toServer(){
        return new Server(
                null,
                model,
                cpu,
                ram,
                disk,
                pricePerMonth,
                Boolean.TRUE.equals(isAvailable)    // 给Available设定初值
        );
    }
}
