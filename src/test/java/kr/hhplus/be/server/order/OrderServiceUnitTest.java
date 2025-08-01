package kr.hhplus.be.server.order;

import kr.baul.server.common.exception.*;
import kr.baul.server.domain.account.Account;
import kr.baul.server.domain.account.AccountReader;
import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import kr.baul.server.domain.coupon.usercoupon.UserCouponReader;
import kr.baul.server.domain.coupon.usercoupon.UserCouponStore;
import kr.baul.server.domain.order.*;
import kr.baul.server.domain.order.orderinfo.OrderInfo;
import kr.baul.server.domain.order.orderinfo.OrderInfoMapper;
import kr.baul.server.domain.order.payment.PaymentProcessor;
import kr.baul.server.domain.order.OrderCommand.RegisterOrder;
import kr.baul.server.domain.order.OrderCommand.RegisterOrder.OrderItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {

    @InjectMocks
    OrderService orderService;

    @Mock
    AccountReader accountReader;

    @Mock
    ItemStockProcessor itemStockProcessor;

    @Mock
    OrderFactory orderFactory;

    @Mock
    PaymentProcessor paymentProcessor;

    @Mock
    OrderInfoMapper orderInfoMapper;

    @Mock
    UserCouponReader userCouponReader;

    @Mock
    UserCouponStore userCouponStore;

    @Mock
    OrderStore orderStore;

    @Test
    void 주문_등록_성공() {
        // given
        Long userId = 1L;
        List<OrderItem> orderItems = List.of(
                new OrderItem(10L, 2, null)
        );
        RegisterOrder registerOrder = new RegisterOrder(userId, orderItems);

        Order dummyOrder = Order.builder()
                .id(1L)
                .userId(userId)
                .totalAmount(200_000L)
                .build();

        Account dummyAccount = Account.builder()
                .userId(userId)
                .balance(100_000L)
                .build();

        OrderInfo.Order dummyOrderInfo = OrderInfo.Order.builder()
                .id(1L)
                .userId(userId)
                .totalAmount(200_000L)
                .build();

        when(orderFactory.store(userId, orderItems)).thenReturn(dummyOrder);
        when(accountReader.getAccount(userId)).thenReturn(dummyAccount);
        when(orderInfoMapper.of(dummyOrder)).thenReturn(dummyOrderInfo);

        // when
        OrderInfo.Order result = orderService.registerOrder(registerOrder);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);

        // verify
        verify(itemStockProcessor).deduct(orderItems);
        verify(paymentProcessor).process(dummyAccount, dummyOrder);
        verify(orderFactory).store(userId, orderItems);
        verify(accountReader).getAccount(userId);
        verify(orderInfoMapper).of(dummyOrder);
    }

    @Test
    void 주문_등록_실패_재고_부족_예외() {
        // given
        Long userId = 1L;
        List<OrderItem> orderItems = List.of(new OrderItem(10L, 2, null));
        RegisterOrder registerOrder = new RegisterOrder(userId, orderItems);

        doThrow(new OutOfStockException()).when(itemStockProcessor).deduct(orderItems);

        // when & then
        assertThatThrownBy(() -> orderService.registerOrder(registerOrder))
                .isInstanceOf(OutOfStockException.class);
    }

    @Test
    void 주문_등록_실패_이미_사용된_쿠폰_예외() {
        // given
        Long userId = 1L;
        List<OrderItem> orderItems = List.of(new OrderItem(10L, 1, 100L)); // couponId 100L
        RegisterOrder registerOrder = new RegisterOrder(userId, orderItems);
        String message = "이미 사용된 쿠폰입니다. userCouponId = " + 100L;

        doThrow(new CouponAlreadyUsedException(message)).when(itemStockProcessor).deduct(orderItems);

        // when & then
        assertThatThrownBy(() -> orderService.registerOrder(registerOrder))
                .isInstanceOf(CouponAlreadyUsedException.class)
                .hasMessageContaining(message); // 메시지 검증
    }

    @Test
    void 주문_등록_실패_사용_중지된_쿠폰_예외() {
        // given
        Long userId = 1L;
        List<OrderItem> orderItems = List.of(new OrderItem(11L, 1, 101L));
        RegisterOrder registerOrder = new RegisterOrder(userId, orderItems);
        String message = "사용이 중지된 쿠폰입니다. couponId = " + 101L;

        doThrow(new CouponDisabledException(message)).when(orderFactory).store(userId, orderItems);

        // when & then
        assertThatThrownBy(() -> orderService.registerOrder(registerOrder))
                .isInstanceOf(CouponDisabledException.class)
                .hasMessageContaining(message);
    }

    @Test
    void 주문_등록_실패_계좌_잔액_부족_예외() {
        // given
        Long userId = 1L;
        List<OrderItem> orderItems = List.of(new OrderItem(12L, 1, null));
        RegisterOrder registerOrder = new RegisterOrder(userId, orderItems);

        Order dummyOrder = Order.builder()
                .id(1L)
                .userId(userId)
                .totalAmount(999_999_999L)
                .build();

        Account dummyAccount = Account.builder()
                .userId(userId)
                .balance(0L)
                .build();

        when(orderFactory.store(userId, orderItems)).thenReturn(dummyOrder);
        when(accountReader.getAccount(userId)).thenReturn(dummyAccount);
        doThrow(new PaymentFailedException()).when(paymentProcessor).process(dummyAccount, dummyOrder);

        // when & then
        assertThatThrownBy(() -> orderService.registerOrder(registerOrder))
                .isInstanceOf(PaymentFailedException.class);
    }

    @Test
    void 주문_등록_결제_실패로_인한_복구_처리() {
        // given
        Long userId = 1L;
        Long couponId = 100L;

        List<OrderItem> orderItems = List.of(new OrderItem(10L, 2, couponId));
        RegisterOrder registerOrder = new RegisterOrder(userId, orderItems);

        Order dummyOrder = Order.builder()
                .id(1L)
                .userId(userId)
                .totalAmount(123_000L)
                .build();

        Account dummyAccount = Account.builder()
                .userId(userId)
                .balance(0L)  // 결제 실패 유도
                .build();

        UserCoupon dummyCoupon = UserCoupon.builder()
                .id(couponId)
                .userId(userId)
                .used(true)
                .build();

        when(orderFactory.store(userId, orderItems)).thenReturn(dummyOrder);
        when(accountReader.getAccount(userId)).thenReturn(dummyAccount);
        when(userCouponReader.getUserCoupon(couponId, userId)).thenReturn(dummyCoupon);

        doThrow(new RuntimeException("결제 실패")).when(paymentProcessor).process(dummyAccount, dummyOrder);

        // when & then
        assertThatThrownBy(() -> orderService.registerOrder(registerOrder))
                .isInstanceOf(PaymentFailedException.class);

        // 복구 관련 메서드들이 호출되었는지 검증
        verify(itemStockProcessor).restore(orderItems);
        verify(userCouponReader).getUserCoupon(couponId, userId);
        verify(userCouponStore).store(dummyCoupon);
        verify(orderStore).store(dummyOrder);

        // 주문 상태가 FAILED로 변경되었는지 확인
        assertThat(dummyOrder.getOrderStatus()).isEqualTo(Order.OrderStatus.FAILED);
    }
}