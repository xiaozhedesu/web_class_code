package club.xiaozhe.cloudservermanager.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "t_server")
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String model;
    private String cpu;
    private String ram;
    private String disk;

    @Column(name = "price_per_month")
    private BigDecimal pricePerMonth;

    @Column(name = "is_available")
    private Boolean isAvailable;
}