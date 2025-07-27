package kr.baul.server.domain.coupon;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class UserCouponDetail {

    @Getter
    @Builder
    public static class UserCouponInfo{
        private Long id;
        private Long userId;
        private Long couponId;
        private boolean used;
        private LocalDateTime usedAt;
        private LocalDateTime createdAt;
    }


}
