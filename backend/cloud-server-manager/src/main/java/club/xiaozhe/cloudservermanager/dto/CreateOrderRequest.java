package club.xiaozhe.cloudservermanager.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(
        @NotNull
        Integer serverId,
        @Positive
        Integer months
) {}
