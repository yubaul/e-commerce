package kr.hhplus.be.server.order;

import kr.baul.server.common.exception.*;
import kr.baul.server.domain.order.OrderCommand.RegisterOrder;
import kr.baul.server.domain.order.OrderCommand.RegisterOrder.OrderItem;
import kr.baul.server.domain.order.OrderService;
import kr.baul.server.domain.order.orderinfo.OrderInfo;
import kr.hhplus.be.server.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(
        classes = kr.baul.server.ServerApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
public class OrderServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    OrderService orderService;

    @Test
    void 주문_등록_성공() {
        // given
        Long userId = 11L;
        List<OrderItem> orderItems = List.of(new OrderItem(500L, 1, null));
        RegisterOrder registerOrder = new RegisterOrder(userId, orderItems);

        // when
        OrderInfo.Order result = orderService.registerOrder(registerOrder);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
    }

    @Test
    void 주문_등록_실패_재고_부족_예외() {
        // given
        Long userId = 11L;
        List<OrderItem> orderItems = List.of(new OrderItem(501L, 999, null));
        RegisterOrder registerOrder = new RegisterOrder(userId, orderItems);

        // when & then
        assertThatThrownBy(() -> orderService.registerOrder(registerOrder))
                .isInstanceOf(OutOfStockException.class);
    }

    @Test
    void 주문_등록_실패_이미_사용된_쿠폰_예외() {
        // given
        Long userId = 11L;
        Long usedCouponId = 100L;
        List<OrderItem> orderItems = List.of(new OrderItem(500L, 1, usedCouponId));
        RegisterOrder registerOrder = new RegisterOrder(userId, orderItems);

        // when & then
        assertThatThrownBy(() -> orderService.registerOrder(registerOrder))
                .isInstanceOf(CouponAlreadyUsedException.class);
    }

    @Test
    void 주문_등록_실패_사용_중지된_쿠폰_예외() {
        // given
        Long userId = 11L;
        Long disabledCouponId = 101L;
        List<OrderItem> orderItems = List.of(new OrderItem(500L, 1, disabledCouponId));
        RegisterOrder registerOrder = new RegisterOrder(userId, orderItems);

        // when & then
        assertThatThrownBy(() -> orderService.registerOrder(registerOrder))
                .isInstanceOf(CouponDisabledException.class);
    }

    @Test
    void 주문_등록_실패_계좌_잔액_부족_예외() {
        // given
        Long userId = 12L;
        List<OrderItem> orderItems = List.of(new OrderItem(500L, 2, null));
        RegisterOrder registerOrder = new RegisterOrder(userId, orderItems);

        // when & then
        assertThatThrownBy(() -> orderService.registerOrder(registerOrder))
                .isInstanceOf(InsufficientBalanceException.class);
    }
}
