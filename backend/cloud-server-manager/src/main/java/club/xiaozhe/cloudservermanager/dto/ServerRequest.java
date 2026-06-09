package club.xiaozhe.cloudservermanager.dto;

import club.xiaozhe.cloudservermanager.entity.Server;

import java.math.BigDecimal;

public record ServerRequest(
        String model,
        String cpu,
        String ram,
        String disk,
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
                isAvailable
        );
    }
}
