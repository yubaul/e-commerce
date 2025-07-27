package kr.hhplus.be.server.accout;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountUnitTest {


    @Test
    void 잔액_충전_예외_없음 (){
        // given
        Long userId = 1L;
        Long amount = 10_000L;

        Account account = new Account(userId);
        // when

        Long balance = account.charge(amount);

        // then
        assertThat(balance).isEqualTo(amount);
    }

    @Test
    void 잔액_조회_예외_없음(){
        // given
        Long userId = 1L;
        Long amount = 10_000L;

        Account account = new Account(userId);
        account.charge(amount);

        // when
        Long balance = account.getBalance();

        // then
        assertThat(balance).isEqualTo(amount);

    }



    class Account{
        private Long userId;
        private Long balance;

        public Account(Long userId){
            this.userId = userId;
            this.balance = 0L;
        }

        public Long charge(Long amount){
            return balance += amount;
        }

        public Long getBalance(){
            return balance;
        }
    }
}
