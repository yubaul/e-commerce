package kr.baul.server.domain.order.payment;

import jakarta.persistence.*;
import kr.baul.server.common.jpa.AbstractEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "payment")
public class Payment extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_method")
    private PayMethod payMethod;

    @Column(name = "pay_amount")
    private Long payAmount;


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
        Long payAmount
    ){
        this.id = id;
        this.orderId = orderId;
        this.payMethod = payMethod;
        this.payAmount = payAmount;
    }
}
