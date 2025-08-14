package kr.baul.server.domain.coupon.usercoupon;

import jakarta.persistence.*;
import kr.baul.server.common.exception.CouponAlreadyUsedException;
import kr.baul.server.common.jpa.AbstractEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_coupon")
public class UserCoupon extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "order_id")
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_coupon_status")
    private UserCouponStatus userCouponStatus;


    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Getter
    @RequiredArgsConstructor
    public static enum UserCouponStatus{
        AVAILABLE("사용 가능"),
        HELD("사용 대기"),
        USED("사용 완료");
        private final String description;
    }

    @Builder
    public UserCoupon(
            Long id,
            Long userId,
            Long couponId
    ){
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.userCouponStatus = UserCouponStatus.AVAILABLE;
    }

    public void hold(Long orderId) {
        if (this.userCouponStatus == UserCouponStatus.USED) {
            throw new CouponAlreadyUsedException("이미 사용된 쿠폰: id=" + id);
        }
        if (this.userCouponStatus == UserCouponStatus.HELD) {
            if (Objects.equals(this.orderId, orderId)) {
                // 같은 주문 재시도
                return;
            }
            throw new CouponAlreadyUsedException("이미 다른 주문에 묶인 쿠폰: heldBy=" + this.orderId);
        }

        this.userCouponStatus = UserCouponStatus.HELD;
        this.orderId = orderId;
        this.usedAt = null;
    }

    public void confirmUse(Long orderId) {
        if (this.userCouponStatus == UserCouponStatus.USED && orderId.equals(this.orderId)) {
            return;
        }
        if (this.userCouponStatus != UserCouponStatus.HELD || !orderId.equals(this.orderId)) {
            throw new IllegalStateException("쿠폰 상태 불일치");
        }
        this.userCouponStatus = UserCouponStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    public void release(Long orderId) {
        if (this.userCouponStatus == UserCouponStatus.HELD && orderId.equals(this.orderId)) {
            this.userCouponStatus = UserCouponStatus.AVAILABLE;
            this.orderId = null;
            this.usedAt = null;
        }
    }


}
