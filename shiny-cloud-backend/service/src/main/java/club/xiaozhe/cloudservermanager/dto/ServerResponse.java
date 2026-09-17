package club.xiaozhe.cloudservermanager.dto;

import club.xiaozhe.cloudservermanager.entity.Server;
import java.math.BigDecimal;

public record ServerResponse(
        Integer id,
        String model,
        String cpu,
        String ram,
        String disk,
        BigDecimal pricePerMonth,
        Boolean isAvailable
) {
    public static ServerResponse from(Server server) {
        return new ServerResponse(
                server.getId(), server.getModel(), server.getCpu(),
                server.getRam(), server.getDisk(), server.getPricePerMonth(),
                server.getIsAvailable()
        );
    }
}
