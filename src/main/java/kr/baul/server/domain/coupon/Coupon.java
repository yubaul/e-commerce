package kr.baul.server.domain.coupon;

import jakarta.persistence.*;
import kr.baul.server.common.exception.CouponDisabledException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long itemId;
    private String name;

    @Column(name = "discount_amount")
    private Long discountAmount;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    private boolean disabled;

    private LocalDateTime issuedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "coupon_id") // CouponStock PK가 coupon_id니까
    private CouponStock couponStock;


    @Builder
    public Coupon(
            Long id,
            Long itemId,
            String name,
            Long discountAmount,
            LocalDate validFrom,
            LocalDate validTo,
            CouponStock couponStock
    ){
        this.id = id;
        this.itemId = itemId;
        this.name = name;
        this.discountAmount = discountAmount;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.couponStock = couponStock;
        this.disabled = false;
    }

    public void issue() {
        couponStock.decrease(1);
    }

    public Long applyDiscount(Long totalPrice){
        if(this.disabled){
            throw new CouponDisabledException("사용이 중지된 쿠폰입니다. couponId = " + id);
        }
        return Math.max(0L, totalPrice - discountAmount);
    }

}
