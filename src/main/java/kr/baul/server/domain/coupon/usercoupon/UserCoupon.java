package kr.baul.server.domain.coupon.usercoupon;

import jakarta.persistence.*;
import kr.baul.server.common.exception.CouponAlreadyUsedException;
import kr.baul.server.common.jpa.AbstractEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private boolean used;

    @Version
    private Long version;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

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
    }

    public void use(){
        if (used) {
            throw new CouponAlreadyUsedException("이미 사용된 쿠폰입니다. userCouponId = " + id);
        }
        this.used = true;
        this.usedAt = LocalDateTime.now();
    }

}
