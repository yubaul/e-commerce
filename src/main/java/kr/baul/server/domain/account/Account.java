package kr.baul.server.domain.account;

import kr.baul.server.common.exception.InsufficientBalanceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Account {

    private Long id;

    private Long userId;

    private Long balance;

    LocalDateTime updatedAt;

    LocalDateTime createdAt;

    @Builder
    public Account(
            Long id,
            Long userId,
            Long balance
    ) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.createdAt = LocalDateTime.now();
    }

    public Long charge(Long amount){
        return balance += amount;
    }

    public Long use(Long amount){
        if(balance < amount){
            throw new InsufficientBalanceException();
        }
        return balance -= amount;
    }


}
