package kr.baul.server.domain.order;

import kr.baul.server.common.exception.PaymentFailedException;
import kr.baul.server.domain.account.Account;
import kr.baul.server.domain.account.AccountReader;
import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import kr.baul.server.domain.coupon.usercoupon.UserCouponReader;
import kr.baul.server.domain.coupon.usercoupon.UserCouponStore;
import kr.baul.server.domain.order.orderinfo.OrderInfo;
import kr.baul.server.domain.order.orderinfo.OrderInfoMapper;
import kr.baul.server.domain.order.payment.PaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final AccountReader accountReader;
    private final ItemStockProcessor itemStockProcessor;
    private final OrderFactory orderFactory;
    private final PaymentProcessor paymentProcessor;
    private final OrderInfoMapper orderInfoMapper;
    private final OrderStore orderStore;
    private final UserCouponReader userCouponReader;
    private final UserCouponStore userCouponStore;

    public OrderInfo.Order registerOrder(OrderCommand.RegisterOrder registerOrder){
        var userId = registerOrder.userId();
        var requestItems = registerOrder.items();

        itemStockProcessor.deduct(requestItems);
        Order order = orderFactory.store(userId, requestItems);

        Account account = accountReader.getAccount(userId);

        try{
            paymentProcessor.process(account, order);
        }catch (Exception e){
            // 결제 실패 시 상품 재고 복구, 주문 상태 실패로 변경, 사용한 쿠폰 미사용으로 변경
            itemStockProcessor.restore(requestItems);
            restoreUsedCoupons(userId, requestItems);
            order.setOrderStatus(Order.OrderStatus.FAILED);
            orderStore.store(order);
            throw new PaymentFailedException();
        }

        return orderInfoMapper.of(order);
    }

    private void restoreUsedCoupons(Long userId, List<OrderCommand.RegisterOrder.OrderItem> items) {
        for (OrderCommand.RegisterOrder.OrderItem item : items) {
            if (item.couponId() != null) {
                UserCoupon userCoupon = userCouponReader.getUserCoupon(item.couponId(), userId);
                userCoupon.restore();
                userCouponStore.store(userCoupon);
            }
        }
    }
}
