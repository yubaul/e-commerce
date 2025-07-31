package kr.baul.server;

import kr.baul.server.domain.account.accounthistory.AccountHistory;
import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import kr.baul.server.domain.order.orderitem.OrderItem;
import kr.baul.server.domain.order.payment.Payment;
import kr.baul.server.domain.order.payment.PaymentRepository;
import kr.baul.server.infrastructure.account.accounthistory.AccountHistoryRepository;
import kr.baul.server.infrastructure.coupon.usercoupon.UserCouponRepository;
import kr.baul.server.infrastructure.order.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Profile("local")
public class UserCouponInitializer implements CommandLineRunner {

    private final UserCouponRepository userCouponRepository;
    private final AccountHistoryRepository accountHistoryRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;

    private static final int BATCH_SIZE = 1_000;
    private static final long TOTAL_COUNT = 100_000;

    @Override
    public void run(String... args) {
        System.out.println("병렬 시딩 시작");

        CompletableFuture<Void> p1 = CompletableFuture.runAsync(this::insertPayments);
        CompletableFuture<Void> p2 = CompletableFuture.runAsync(this::insertOrderItems);
        CompletableFuture<Void> p3 = CompletableFuture.runAsync(this::insertAccountHistories);
        CompletableFuture<Void> p4 = CompletableFuture.runAsync(this::insertUserCoupons);

        CompletableFuture.allOf(p1, p2, p3, p4).join();
        System.out.println("전체 시딩 완료");
    }

    private void insertPayments() {
        List<Payment> bulk = new ArrayList<>();
        for (long i = 1; i <= TOTAL_COUNT; i++) {
            bulk.add(Payment.builder()
                    .orderId(i)
                    .payMethod(Payment.PayMethod.CARD)
                    .payAmount(1_000L)
                    .build());

            if (bulk.size() == BATCH_SIZE) {
                paymentRepository.saveAll(bulk);
                bulk.clear();
                log("Payment", i);
            }
        }
        if (!bulk.isEmpty()) paymentRepository.saveAll(bulk);
    }

    private void insertOrderItems() {
        List<OrderItem> bulk = new ArrayList<>();
        for (long i = 1; i <= TOTAL_COUNT; i++) {
            bulk.add(OrderItem.builder()
                    .orderId(i)
                    .itemId(i)
                    .quantity(1)
                    .itemPriceAtOrder(1_000L)
                    .build());

            if (bulk.size() == BATCH_SIZE) {
                orderItemRepository.saveAll(bulk);
                bulk.clear();
                log("OrderItem", i);
            }
        }
        if (!bulk.isEmpty()) orderItemRepository.saveAll(bulk);
    }

    private void insertAccountHistories() {
        List<AccountHistory> bulk = new ArrayList<>();
        for (long i = 1; i <= TOTAL_COUNT; i++) {
            bulk.add(AccountHistory.builder()
                    .accountId(i)
                    .amount(1_000L)
                    .balance(100_000L)
                    .transactionType(AccountHistory.TransactionType.CHARGE)
                    .sourceType(AccountHistory.SourceType.MANUAL)
                    .paymentId(i)
                    .build());

            if (bulk.size() == BATCH_SIZE) {
                accountHistoryRepository.saveAll(bulk);
                bulk.clear();
                log("AccountHistory", i);
            }
        }
        if (!bulk.isEmpty()) accountHistoryRepository.saveAll(bulk);
    }

    private void insertUserCoupons() {
        List<UserCoupon> bulk = new ArrayList<>();
        for (long i = 1; i <= TOTAL_COUNT; i++) {
            bulk.add(UserCoupon.builder()
                    .userId(i)
                    .couponId(i)
                    .used(false)
                    .build());

            if (bulk.size() == BATCH_SIZE) {
                userCouponRepository.saveAll(bulk);
                bulk.clear();
                log("UserCoupon", i);
            }
        }
        if (!bulk.isEmpty()) userCouponRepository.saveAll(bulk);
    }

    private void log(String table, long count) {
        if (count % (BATCH_SIZE * 10) == 0) {
            System.out.printf("%s: %,d건 저장 완료%n", table, count);
        }
    }
}