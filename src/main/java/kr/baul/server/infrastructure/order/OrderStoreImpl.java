package kr.baul.server.infrastructure.order;

import kr.baul.server.domain.order.Order;
import kr.baul.server.domain.order.OrderStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStoreImpl implements OrderStore {

    private final OrderRepository orderRepository;

    @Override
    public Order store(Order order) {
        return orderRepository.save(order);
    }
}
