package kr.baul.server.domain.coupon;

import kr.baul.server.common.exception.OutOfStockCouponException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class Coupon {

    private Long id;
    private String name;
    private Long discountAmount;
    private LocalDate validFrom;
    private LocalDate validTo;
    private int quantity;
    private boolean used;
    private LocalDateTime issuedAt;
    private LocalDateTime createdAt;


    @Builder
    public Coupon(
            Long id,
            String name,
            Long discountAmount,
            LocalDate validFrom,
            LocalDate validTo,
            int quantity
    ){
        LocalDateTime now = LocalDateTime.now();
        this.id = id;
        this.name = name;
        this.discountAmount = discountAmount;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.quantity = quantity;
        this.issuedAt = now;
        this.createdAt = now;
    }

    public void useOne() {
        if (quantity <= 0) {
            throw new OutOfStockCouponException();
        }
        quantity -= 1;
    }

}
