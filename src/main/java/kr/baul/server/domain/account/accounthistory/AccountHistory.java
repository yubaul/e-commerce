package kr.baul.server.domain.account.accounthistory;

import jakarta.persistence.*;
import kr.baul.server.common.jpa.AbstractEntity;
import lombok.*;


@Getter
@Entity
@NoArgsConstructor
@Table(name = "account_history")
public class AccountHistory extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id")
    private Long accountId;
    private Long amount;
    private Long balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type")
    private SourceType sourceType;

    @Column(name = "payment_id")
    private Long paymentId;



    @Getter
    @RequiredArgsConstructor
    public enum TransactionType{
        CHARGE("잔액 충전"),
        USE("잔액 사용");

        private final String description;

    }

    @Getter
    @RequiredArgsConstructor
    public enum SourceType{
        PAY("주문 결제"),
        MANUAL("사용자 수동");

        private final String description;
    }

    @Builder
    public AccountHistory(
            Long accountId,
            Long amount,
            Long balance,
            TransactionType transactionType,
            SourceType sourceType,
            Long paymentId
    ){
        this.accountId = accountId;
        this.amount = amount;
        this.balance = balance;
        this.transactionType = transactionType;
        this.sourceType = sourceType;
        this.paymentId = paymentId;
    }



}
