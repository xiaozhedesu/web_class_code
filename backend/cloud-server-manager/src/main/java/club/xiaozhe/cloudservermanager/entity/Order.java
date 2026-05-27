package club.xiaozhe.cloudservermanager.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "server_id")
    private Integer serverId;

    private Integer months;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    private String status;

    @CreationTimestamp
    @Column(name = "order_time", updatable = false)
    private LocalDateTime orderTime;
}