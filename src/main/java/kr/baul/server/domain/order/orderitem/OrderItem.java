package kr.baul.server.domain.order.orderitem;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long itemId;
    private int quantity;
    private Long itemPriceAtOrder;
    private LocalDateTime createdAt;

    @Builder
    public OrderItem(
            Long id,
            Long orderId,
            Long itemId,
            int quantity,
            Long itemPriceAtOrder
    ){
        this.id = id;
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.itemPriceAtOrder = itemPriceAtOrder;
        this.createdAt = LocalDateTime.now();
    }

}
