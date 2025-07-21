package kr.hhplus.be.server.accout;

import static kr.baul.server.interfaces.account.AccountDto.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import kr.baul.server.application.account.AccountService;
import kr.baul.server.common.exception.EntityNotFoundException;
import kr.baul.server.domain.account.Account;
import kr.baul.server.domain.account.AccountReader;
import kr.baul.server.domain.account.AccountStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountServiceUnitTest {

    @InjectMocks
    AccountService accountService;

    @Mock
    AccountReader accountReader;

    @Mock
    AccountStore accountStore;


    @Test
    void 잔액_충전_예외_없음(){
        // given
        Long userId = 1L;
        Long amount = 50_000L;
        AccountChargeRequest request = new AccountChargeRequest();
        request.setUserId(userId);
        request.setAmount(amount);

        Account account = Account.builder()
                .userId(1L)
                .balance(0L)
                .build();


        when(accountReader.getAccount(account.getUserId())).thenReturn(account);
        when(accountStore.store(account)).thenReturn(account);

        // when
        Long balance = accountService.charge(request);

        // then
        assertThat(balance).isEqualTo(amount);
    }

    @Test
    void 잔액_충전_계좌없음_예외() {
        // given
        Long userId = 1L;
        Long amount = 10_000L;
        AccountChargeRequest request = new AccountChargeRequest();
        request.setUserId(userId);
        request.setAmount(amount);

        when(accountReader.getAccount(userId))
                .thenThrow(new EntityNotFoundException("해당 사용자에게 등록된 계좌가 없습니다."));

        // when & then
        assertThatThrownBy(() -> accountService.charge(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 사용자에게 등록된 계좌가 없습니다.");
    }

    @Test
    void 잔액_조회_예외_없음(){
        // given
        Long userId = 1L;
        Long balance = 10_000L;
        Account account = Account.builder()
                .userId(1L)
                .balance(balance)
                .build();
        when(accountReader.getAccount(userId)).thenReturn(account);

        // when
        Long result = accountService.getAccountBalance(userId);

        // then
        assertThat(result).isEqualTo(balance);
    }

    @Test
    void 잔액_조회_계좌없음_예외() {
        // given
        Long userId = 1L;

        when(accountReader.getAccount(userId))
                .thenThrow(new EntityNotFoundException("해당 사용자에게 등록된 계좌가 없습니다."));

        // when & then
        assertThatThrownBy(() -> accountService.getAccountBalance(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 사용자에게 등록된 계좌가 없습니다.");
    }

}
