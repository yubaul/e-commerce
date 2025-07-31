package kr.baul.server.domain.coupon;

import jakarta.persistence.*;
import kr.baul.server.common.exception.OutOfStockException;
import kr.baul.server.common.jpa.AbstractEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "coupon_stock")
public class CouponStock extends AbstractEntity {

    @Id
    @Column(name = "coupon_id")
    private Long couponId;

    private int quantity;

    @Builder
    public CouponStock(Long couponId, int quantity) {
        this.couponId = couponId;
        this.quantity = quantity;
    }

    public void decrease(int amount) {
        if (quantity < amount) {
            throw new OutOfStockException("쿠폰 ID: " + couponId + ", 요청 수량: " + amount + " → 현재 수량: " + quantity + " (부족)");
        }
        this.quantity -= amount;
    }

    public void increase(int amount) {
        this.quantity += amount;
    }
}