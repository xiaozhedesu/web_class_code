package club.xiaozhe.cloudservermanager.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String password;
    private String realName;
    private String phone;
    private String role;

    @Column(name = "create_time", insertable = false, updatable = false)
    private LocalDateTime createTime;
}