package kr.baul.server.domain.coupon.usercoupon;

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
            Long couponId
    ){
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.createdAt = LocalDateTime.now();
    }
}
