package kr.baul.server.domain.coupon.issuing;

public interface CouponFinalizer {
    void finalizeIssue(Long couponId, Long userId);
}
