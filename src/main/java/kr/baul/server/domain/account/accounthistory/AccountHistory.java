package kr.baul.server.domain.account.accounthistory;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountHistory {

    private Long id;
    private Long accountId;
    private Long amount;
    private Long balance;
    private TransactionType transactionType;
    private SourceType sourceType;
    private LocalDateTime createdAt;



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
            Long id,
            Long accountId,
            Long amount,
            Long balance,
            TransactionType transactionType,
            SourceType sourceType
    ){
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.balance = balance;
        this.transactionType = transactionType;
        this.sourceType = sourceType;
        this.createdAt = LocalDateTime.now();
    }



}
