package kr.baul.server.domain.coupon.usercoupon;

import kr.baul.server.common.exception.CouponAlreadyUsedException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UserCoupon {
    private Long id;
    private Long userId;
    private Long couponId;
    private boolean used;
    private LocalDateTime usedAt;
    private LocalDateTime createdAt;

    @Builder
    public UserCoupon(
            Long id,
            Long userId,
            Long couponId,
            boolean used
    ){
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.used = used;
        this.createdAt = LocalDateTime.now();
    }

    public void use(){
        if (used) {
            throw new CouponAlreadyUsedException("이미 사용된 쿠폰입니다. userCouponId = " + id);
        }
        this.used = true;
        this.usedAt = LocalDateTime.now();
    }

    public void restore(){
        this.used = false;
    }
}
