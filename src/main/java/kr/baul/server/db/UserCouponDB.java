package kr.baul.server.db;

import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UserCouponDB {

    private final Map<Long, List<UserCoupon>> table = new HashMap<>();
    private static AtomicInteger integer = new AtomicInteger(1);

    public Optional<List<UserCoupon>> selectByUserId(Long userId) {
        throttle(200);
        return Optional.ofNullable(table.get(userId));
    }

    public UserCoupon insert(UserCoupon userCoupon) {
        throttle(300);
        Long userId = userCoupon.getUserId();
        table.computeIfAbsent(userId, k -> new ArrayList<>()).add(userCoupon);
        return userCoupon;
    }

    public static long getAtomicInteger(){
        return integer.getAndIncrement();
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {

        }
    }

}
