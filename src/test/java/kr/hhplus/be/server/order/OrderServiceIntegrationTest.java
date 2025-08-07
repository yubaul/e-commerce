package kr.hhplus.be.server.order;

import kr.baul.server.common.exception.*;
import kr.baul.server.domain.account.Account;
import kr.baul.server.domain.account.AccountReader;
import kr.baul.server.domain.item.ItemStock;
import kr.baul.server.domain.order.OrderCommand.RegisterOrder;
import kr.baul.server.domain.order.OrderCommand.RegisterOrder.OrderItem;
import kr.baul.server.domain.order.OrderService;
import kr.baul.server.domain.order.orderinfo.OrderInfo;
import kr.baul.server.infrastructure.item.ItemStockRepository;
import kr.baul.server.infrastructure.order.OrderRepository;
import kr.hhplus.be.server.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
public class OrderServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    OrderService orderService;

    @Autowired
    ItemStockRepository itemStockRepository;

    @Autowired
    AccountReader accountReader;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void 주문_등록_성공() {
        // given
        Long userId = 11L;
        List<OrderItem> orderItems = List.of(new OrderItem(500L, 1, null));
        RegisterOrder registerOrder = new RegisterOrder(userId, orderItems);

        // when
        OrderInfo.Order result = orderService.registerOrder(registerOrder);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
    }

    @Test
    void 주문_등록_실패_재고_부족_예외() {
        // given
        Long userId = 11L;
        List<OrderItem> orderItems = List.of(new OrderItem(501L, 999, null));
        RegisterOrder registerOrder = new RegisterOrder(userId, orderItems);

        // when & then
        assertThatThrownBy(() -> orderService.registerOrder(registerOrder))
                .isInstanceOf(OutOfStockException.class);
    }

    @Test
    void 주문_등록_실패_사용_중지된_쿠폰_예외() {
        // given
        Long userId = 11L;
        Long disabledCouponId = 101L;
        List<OrderItem> orderItems = List.of(new OrderItem(500L, 1, disabledCouponId));
        RegisterOrder registerOrder = new RegisterOrder(userId, orderItems);

        // when & then
        assertThatThrownBy(() -> orderService.registerOrder(registerOrder))
                .isInstanceOf(CouponDisabledException.class);
    }

    @Test
    void 주문_등록_실패_계좌_잔액_부족_예외() {
        // given
        Long userId = 12L;
        List<OrderItem> orderItems = List.of(new OrderItem(500L, 2, null));
        RegisterOrder registerOrder = new RegisterOrder(userId, orderItems);

        // when & then
        assertThatThrownBy(() -> orderService.registerOrder(registerOrder))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    void 동시_주문_요청시_재고_수량만큼만_주문_성공() throws InterruptedException {
        // given
        final int threadCount = 5;
        final Long itemId = 600L; // 재고 5개로 사전 세팅된 상품
        final ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final Long userId = 600L + i;

            executor.submit(() -> {
                try {
                    RegisterOrder command = new RegisterOrder(
                            userId,
                            List.of(new OrderItem(itemId, 1, null))
                    );

                    orderService.registerOrder(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // then
        assertThat(successCount.get())
                .as("재고 수량만큼 성공해야 함")
                .isEqualTo(threadCount);

        ItemStock stock = itemStockRepository.findById(itemId).get();
        assertThat(stock.getQuantity())
                .as("남은 재고는 0이어야 함")
                .isEqualTo(0);
    }

    @Test
    void 동시_주문_요청시_재고_초과_예외() throws InterruptedException {
        // given
        int threadCount = 5;
        Long itemId = 650L; // 재고 4개로 사전 세팅된 상품
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final Long userId = 650L + i;

            executor.submit(() -> {
                try {
                    RegisterOrder command = new RegisterOrder(
                            userId,
                            List.of(new OrderItem(itemId, 1, null))
                    );
                    orderService.registerOrder(command);
                    successCount.incrementAndGet();
                } catch (OutOfStockException e) {
                    failureCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // then
        assertThat(successCount.get())
                .as("재고 수량 만큼만 성공해야 함")
                .isEqualTo(4);

        assertThat(failureCount.get())
                .as("초과 주문 수 만큼 실패해야 함")
                .isEqualTo(1);

        ItemStock stock = itemStockRepository.findById(itemId).get();
        assertThat(stock.getQuantity())
                .as("남은 재고는 0이어야 함")
                .isEqualTo(0);
    }

    @Test
    void 동시_쿠폰_사용_요청_시_하나만_할인_적용되고_나머지는_정가로_주문됨() throws InterruptedException {
        // given
        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger discountedCount = new AtomicInteger(0);
        AtomicInteger fullPriceCount = new AtomicInteger(0);

        Long userId = 670L;
        Long itemId = 670L;            // 상품 가격: 10,000원
        Long userCouponId = 670L;
        Long discountPrice = 5_000L;
        Long fullPrice = 10_000L;

        List<OrderInfo.Order> results = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    RegisterOrder order = new RegisterOrder(
                            userId,
                            List.of(new OrderItem(itemId, 1, userCouponId))
                    );
                    OrderInfo.Order result = orderService.registerOrder(order);
                    results.add(result);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // then
        assertThat(results).hasSize(threadCount);

        for (OrderInfo.Order order : results) {
            if (order.totalAmount().equals(discountPrice)) {
                discountedCount.incrementAndGet();
            } else if (order.totalAmount().equals(fullPrice)) {
                fullPriceCount.incrementAndGet();
            }
        }

        assertThat(discountedCount.get())
                .as("오직 하나만 쿠폰 적용")
                .isEqualTo(1);

        assertThat(fullPriceCount.get())
                .as("나머지는 정가")
                .isEqualTo(threadCount - 1);
    }

    @Test
    void 동시_잔액_차감_시_한도_이내만_성공() throws InterruptedException {
        // given
        final Long userId = 660L; // 잔액 30,000원 계좌를 가진 유저
        final Long itemId = 660L;  // 가격 5,000원 상품 (재고 충분해야 함)
        final int threadCount = 4;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    RegisterOrder order = new RegisterOrder(
                            userId,
                            List.of(new OrderItem(itemId, 1, null))
                    );
                    orderService.registerOrder(order);
                    successCount.incrementAndGet();
                } catch (InsufficientBalanceException e) {
                    failureCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // then
        assertThat(successCount.get())
                .as("두 번까지만 결제 성공해야 함")
                .isEqualTo(3);

        assertThat(failureCount.get())
                .as("잔액 부족으로 한 번 실패해야 함")
                .isEqualTo(1);

        Account account = accountReader.getAccount(userId);
        assertThat(account.getBalance())
                .as("잔액은 0이어야 함 (1만원 * 3회 차감)")
                .isEqualTo(0);
    }

}
