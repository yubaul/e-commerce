package kr.baul.server.domain.coupon;

import kr.baul.server.common.exception.CouponDisabledException;
import kr.baul.server.common.exception.OutOfStockException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class Coupon {

    private Long id;
    private Long itemId;
    private String name;
    private Long discountAmount;
    private LocalDate validFrom;
    private LocalDate validTo;
    private int quantity;
    private boolean disabled;
    private LocalDateTime issuedAt;
    private LocalDateTime createdAt;


    @Builder
    public Coupon(
            Long id,
            Long itemId,
            String name,
            Long discountAmount,
            LocalDate validFrom,
            LocalDate validTo,
            int quantity
    ){
        LocalDateTime now = LocalDateTime.now();
        this.id = id;
        this.itemId = itemId;
        this.name = name;
        this.discountAmount = discountAmount;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.quantity = quantity;
        this.disabled = false;
        this.issuedAt = now;
        this.createdAt = now;
    }

    public void issue() {
        if (quantity <= 0) {
            String message = "쿠폰 ID : " + this.id + ",  쿠폰 재고가 부족합니다.";
            throw new OutOfStockException(message);
        }
        quantity -= 1;
    }

    public Long applyDiscount(Long totalPrice){
        if(this.disabled){
            throw new CouponDisabledException("사용이 중지된 쿠폰입니다. couponId = " + id);
        }
        return Math.max(0L, totalPrice - discountAmount);
    }

}
