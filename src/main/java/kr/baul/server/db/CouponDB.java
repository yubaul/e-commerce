package kr.baul.server.db;

import jakarta.annotation.PostConstruct;
import kr.baul.server.domain.coupon.Coupon;
import kr.baul.server.domain.coupon.CouponStock;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class CouponDB {

    private final Map<Long, Coupon> table = new HashMap<>();

    @PostConstruct
    public void init(){
        CouponStock couponStock1 = CouponStock.builder()
                .couponId(1L)
                .quantity(10)
                .build();
        Coupon coupon1 = Coupon.builder()
                .id(1L)
                .itemId(1L)
                .name("질레트 한정 면도날 할인 쿠폰")
                .discountAmount(10_000L)
                .validFrom(LocalDate.now())
                .validTo(LocalDate.now().plusDays(7))
                .couponStock(couponStock1)
                .build();

        CouponStock couponStock2 = CouponStock.builder()
                .couponId(2L)
                .quantity(3)
                .build();
        Coupon coupon2 = Coupon.builder()
                .id(2L)
                .itemId(2L)
                .name("선착순 할인 쿠폰")
                .discountAmount(30_000L)
                .validFrom(LocalDate.now())
                .validTo(LocalDate.now().plusDays(7))
                .couponStock(couponStock2)
                .build();
        table.put(coupon1.getId(), coupon1);
        table.put(coupon2.getId(), coupon2);
    }



    public Optional<Coupon> selectById(Long couponId) {
        throttle(200);
        return Optional.ofNullable(table.get(couponId));
    }

    public Coupon insert(Coupon coupon) {
        throttle(300);
        return table.put(coupon.getId(), coupon);
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {

        }
    }

}
