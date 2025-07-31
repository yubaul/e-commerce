package kr.baul.server.domain.order.orderitem;

import jakarta.persistence.*;
import kr.baul.server.common.jpa.AbstractEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "item_id")
    private Long itemId;

    private int quantity;

    @Column(name = "item_price_at_order")
    private Long itemPriceAtOrder;

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
    }

}
