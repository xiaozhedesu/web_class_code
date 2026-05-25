package club.xiaozhe.cloudservermanager.repository;

import club.xiaozhe.cloudservermanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Page<User> findByRealNameContaining(String realName, Pageable pageable);
}