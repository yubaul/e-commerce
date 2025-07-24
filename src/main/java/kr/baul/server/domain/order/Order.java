package kr.baul.server.domain.order;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class Order {

    private Long id;
    private Long userId;
    private Long totalAmount;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;

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
            Long id,
            Long userId,
            Long totalAmount
    ){
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderStatus = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();

    }

    public void setTotalAmount(Long totalAmount){
        this.totalAmount = totalAmount;
    }

    public void setOrderStatus(OrderStatus orderStatus){
        this.orderStatus = orderStatus;
    }

}
