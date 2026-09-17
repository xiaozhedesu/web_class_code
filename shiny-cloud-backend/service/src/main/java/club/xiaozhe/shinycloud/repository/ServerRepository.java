package club.xiaozhe.shinycloud.repository;

import club.xiaozhe.shinycloud.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<Server, Integer> {
}