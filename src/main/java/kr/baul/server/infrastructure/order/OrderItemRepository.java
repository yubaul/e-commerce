package kr.baul.server.infrastructure.order;

import kr.baul.server.domain.order.orderitem.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
