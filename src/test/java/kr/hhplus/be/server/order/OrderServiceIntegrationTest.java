package kr.hhplus.be.server.order;

import kr.baul.server.common.exception.*;
import kr.baul.server.domain.account.Account;
import kr.baul.server.domain.account.AccountReader;
import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import kr.baul.server.domain.item.ItemStock;
import kr.baul.server.domain.order.Order;
import kr.baul.server.domain.order.OrderCommand.RegisterOrder;
import kr.baul.server.domain.order.OrderCommand.RegisterOrder.OrderItem;
import kr.baul.server.domain.order.OrderService;
import kr.baul.server.domain.order.orderinfo.OrderInfo;
import kr.baul.server.infrastructure.coupon.usercoupon.UserCouponRepository;
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

    @Autowired OrderService orderService;

    @Autowired ItemStockRepository itemStockRepository;

    @Autowired AccountReader accountReader;

    @Autowired UserCouponRepository userCouponRepository;

    @Autowired OrderRepository orderRepository;


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
    void 동시_다중상품_중간_재고_품절시_이전_차감_롤백() {
        // given
        // 재고 1개씩 가진 5개 상품 세팅
        Long userId = 610L;
        List<Long> itemIds = List.of(610L, 611L, 612L, 613L, 614L);

        // 주문: 앞의 3개는 1개씩 정상 차감, 4번째에서 2개를 요구해 품절 유도, 5번째는 도달하지 않음
        List<OrderItem> orderItems = List.of(
                new OrderItem(itemIds.get(0), 1, null),
                new OrderItem(itemIds.get(1), 1, null),
                new OrderItem(itemIds.get(2), 1, null),
                new OrderItem(itemIds.get(3), 2, null), // ← 여기서 OutOfStockException 발생
                new OrderItem(itemIds.get(4), 1, null)
        );
        RegisterOrder registerOrder = new RegisterOrder(userId, orderItems);

        // when & then
        // 품절 예외가 발생해야 한다
        assertThatThrownBy(() -> orderService.registerOrder(registerOrder))
                .isInstanceOf(OutOfStockException.class);

        // 이전에 차감됐던 3개도 모두 원복되어, 전 상품 재고가 그대로 1이어야 한다
        for (Long itemId : itemIds) {
            ItemStock s = itemStockRepository.findById(itemId).get();
            assertThat(s.getQuantity())
                    .as("롤백 후 재고는 1이어야 함 (itemId=%s)", itemId)
                    .isEqualTo(1);
        }

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
    void 결제_실패시_쿠폰_해제_및_재고_복구_그리고_주문_상태_FAILED() {
        // given
        // 잔액 부족을 유도할 사용자/상품/쿠폰
        Long userId = 680L;          // 잔액이 적거나 결제 실패 유도 가능한 유저
        Long itemId = 680L;          // 재고 1 이상 존재
        Long couponId = 680L;    // 해당 유저가 보유 중(AVAILABLE)인 쿠폰의 coupon_id (user_coupon.coupon_id)

        // 주문은 1개만 구매 + 쿠폰 사용 (결제 단계에서 InsufficientBalanceException을 기대)
        RegisterOrder cmd = new RegisterOrder(
                userId,
                List.of(new OrderItem(itemId, 1, couponId))
        );

        // 현재 재고 스냅샷
        int beforeQty = itemStockRepository.findById(itemId)
                .orElseThrow()
                .getQuantity();

        // 쿠폰은 AVAILABLE 상태여야 함(사전 보장)
        UserCoupon before = userCouponRepository.findByCouponIdAndUserId(couponId, userId)
                .orElseThrow(() -> new IllegalStateException("사전 쿠폰 픽스처 없음"));
        assertThat(before.getUserCouponStatus()).isEqualTo(UserCoupon.UserCouponStatus.AVAILABLE);
        assertThat(before.getOrderId()).isNull();

        // when
        assertThatThrownBy(() -> orderService.registerOrder(cmd))
                .as("결제 단계에서 실패(잔액 부족 등)해야 함")
                .isInstanceOf(InsufficientBalanceException.class);

        // then
        // 재고 원복: 차감되었다가 보상 로직으로 복구되어 beforeQty와 동일해야 함
        int afterQty = itemStockRepository.findById(itemId)
                .orElseThrow()
                .getQuantity();
        assertThat(afterQty).as("결제 실패 시 재고는 원복되어야 함").isEqualTo(beforeQty);

        // 쿠폰 RELEASE: 이 주문에서 HELD했던 쿠폰이 다시 AVAILABLE로 돌아가고 order_id가 null이어야 함
        UserCoupon after = userCouponRepository.findByCouponIdAndUserId(couponId, userId)
                .orElseThrow();
        assertThat(after.getUserCouponStatus()).isEqualTo(UserCoupon.UserCouponStatus.AVAILABLE);
        assertThat(after.getOrderId()).as("보상 후 쿠폰은 특정 주문에 묶여있지 않아야 함").isNull();

        // 주문 FAILED: 최신 주문 조회해서 상태가 FAILED인지 확인
        Order failedOrder = orderRepository.findByUserIdAndOrderStatus(userId, Order.OrderStatus.FAILED)
                .stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("주문이 생성되지 않았음"));
        assertThat(failedOrder.getOrderStatus()).isEqualTo(Order.OrderStatus.FAILED);
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
