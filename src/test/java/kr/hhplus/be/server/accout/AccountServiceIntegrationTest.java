package kr.hhplus.be.server.accout;

import kr.baul.server.application.account.AccountCommand;
import kr.baul.server.application.account.AccountService;
import kr.baul.server.common.exception.EntityNotFoundException;
import kr.hhplus.be.server.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(
        classes = kr.baul.server.ServerApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
public class AccountServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    AccountService accountService;

    @Test
    void 잔액_충전_예외_없음() {
        // given
        Long userId = 40L;
        Long amount = 50_000L;

        AccountCommand.AccountCharge command = AccountCommand.AccountCharge.builder()
                .userId(userId)
                .amount(amount)
                .build();

        // when
        Long result = accountService.charge(command);

        // then
        assertThat(result).isEqualTo(amount);
    }

    @Test
    void 잔액_충전_계좌없음_예외() {
        // given
        Long userId = 9999L;
        Long amount = 10_000L;

        AccountCommand.AccountCharge command = AccountCommand.AccountCharge.builder()
                .userId(userId)
                .amount(amount)
                .build();

        // when & then
        assertThatThrownBy(() -> accountService.charge(command))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 사용자에게 등록된 계좌가 없습니다.");
    }

    @Test
    void 잔액_조회_예외_없음() {
        // given
        Long userId = 20L;

        // when
        Long balance = accountService.getAccountBalance(userId);

        // then
        assertThat(balance).isEqualTo(10000L);
    }

    @Test
    void 잔액_조회_계좌없음_예외() {
        // given
        Long userId = 8888L;

        // when & then
        assertThatThrownBy(() -> accountService.getAccountBalance(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 사용자에게 등록된 계좌가 없습니다.");
    }

    @Test
    void 계좌_잔액_충전_동시성_테스트() throws Exception {
        // given
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger failureCount = new AtomicInteger(0);
        Long userId = 500L;
        Long amount = 10_000L;
        Long totalAmount = threadCount * amount;

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    AccountCommand.AccountCharge command = AccountCommand.AccountCharge.builder()
                            .userId(userId)
                            .amount(amount)
                            .build();

                    accountService.charge(command);
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Long result = accountService.getAccountBalance(userId);
        assertThat(result).isEqualTo(totalAmount);
        assertThat(failureCount.get()).isEqualTo(0);
    }
}