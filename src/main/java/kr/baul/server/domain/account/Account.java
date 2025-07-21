package kr.baul.server.domain.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Account {


    private Long userId;

    private Long balance;

    LocalDateTime updatedAt;

    LocalDateTime createdAt;

    @Builder
    public Account(Long userId,
                   Long balance
    ) {
        this.userId = userId;
        this.balance = balance;
        this.createdAt = LocalDateTime.now();
    }

    public Long charge(Long amount){
        return balance += amount;
    }


}
