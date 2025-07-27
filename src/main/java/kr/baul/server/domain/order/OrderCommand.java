package kr.baul.server.domain.order;

import lombok.Builder;

import java.util.List;

public class OrderCommand {

    @Builder
    public record RegisterOrder(
            Long userId,
            List<OrderItem> items
    ) {
        @Builder
        public record OrderItem(
                Long itemId,
                Integer quantity,
                Long couponId
        ) {}
    }
}
