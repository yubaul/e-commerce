package kr.baul.server.domain.account;

import jakarta.persistence.*;
import kr.baul.server.common.exception.InsufficientBalanceException;
import kr.baul.server.common.jpa.AbstractEntity;
import kr.baul.server.domain.account.accounthistory.AccountHistory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@Getter
@Entity
@Table(name = "accounts")
public class Account extends AbstractEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long balance;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private List<AccountHistory> accountHistory;

    @Builder
    public Account(
            Long id,
            Long userId,
            Long balance
    ) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
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
