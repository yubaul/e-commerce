package kr.baul.server.domain.coupon;

import lombok.Builder;

public record CouponCommand()
{
    @Builder
    public record IssueCoupon(
            Long userId,
            Long couponId
    ){

    }
}
