package kr.hhplus.be.server.coupon;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class CouponUnitTest {

    @Test
    void 쿠폰_재고_차감_정상() {
        // given
        Coupon coupon = new Coupon(1L, "할인쿠폰", 5);

        // when
        coupon.useOne();

        // then
        assertThat(coupon.getQuantity()).isEqualTo(4);
    }

    @Test
    void 쿠폰_재고_없을_때_예외_발생() {
        // given
        Coupon coupon = new Coupon(1L, "할인쿠폰", 0);

        // when & then
        assertThatThrownBy(coupon::useOne)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("쿠폰 재고가 부족합니다.");
    }

    private class Coupon {
        Long id;
        String name;
        int quantity;

        public Coupon(Long id, String name, int quantity) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
        }

        public void useOne() {
            if (quantity <= 0) {
                throw new RuntimeException("쿠폰 재고가 부족합니다.");
            }
            quantity -= 1;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}