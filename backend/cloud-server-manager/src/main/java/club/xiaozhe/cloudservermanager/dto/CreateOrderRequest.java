package club.xiaozhe.cloudservermanager.dto;

public record CreateOrderRequest(
        Integer serverId,
        Integer months
) {}
