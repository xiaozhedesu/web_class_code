package club.xiaozhe.cloudservermanager.repository;

import club.xiaozhe.cloudservermanager.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<Server, Integer> {
}