package kr.baul.server.domain.order;

import jakarta.persistence.*;
import kr.baul.server.common.jpa.AbstractEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


@Getter
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "total_amount")
    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;


    @Getter
    @RequiredArgsConstructor
    public static enum OrderStatus{
        CREATED("주문 생성"),
        PAID("결제 완료"),
        FAILED("주문 실패"),
        CANCELED("주문 취소");

        private final String description;
    }

    @Builder
    public Order(
            Long userId,
            Long totalAmount
    ){
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderStatus = OrderStatus.CREATED;

    }

    public void setTotalAmount(Long totalAmount){
        this.totalAmount = totalAmount;
    }

    public void setOrderStatus(OrderStatus orderStatus){
        this.orderStatus = orderStatus;
    }

}
