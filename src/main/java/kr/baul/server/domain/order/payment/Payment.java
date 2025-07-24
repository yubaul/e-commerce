package kr.baul.server.domain.order.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class Payment {
    private Long id;
    private Long orderId;
    private Long accountHistoryId;
    private PayMethod payMethod;
    private Long payAmount;
    private LocalDateTime createdAt;

    @Getter
    @RequiredArgsConstructor
    public static enum PayMethod{
        CARD("카드"),
        ACCOUNT("계좌"),
        POINT("포인트");

        private final String description;
    }

    @Builder
    public Payment(
        Long id,
        Long orderId,
        PayMethod payMethod,
        Long payAmount,
        Long accountHistoryId
    ){
        this.id = id;
        this.orderId = orderId;
        this.payMethod = payMethod;
        this.payAmount = payAmount;
    }
}
