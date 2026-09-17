package club.xiaozhe.cloudservermanager.repository;

import club.xiaozhe.cloudservermanager.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(Integer userId);
    Page<Order> findAll(Pageable pageable);
}