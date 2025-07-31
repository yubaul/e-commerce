package kr.baul.server.infrastructure.order;

import kr.baul.server.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
