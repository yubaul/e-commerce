package kr.baul.server.infrastructure.order;

import kr.baul.server.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByUserIdAndOrderStatus(Long userId, Order.OrderStatus orderStatus);
}
