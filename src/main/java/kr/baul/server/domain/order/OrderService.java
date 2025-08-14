package kr.baul.server.domain.order;

import kr.baul.server.domain.order.itemstock.ItemStockProcessor;
import kr.baul.server.domain.order.orderinfo.OrderInfo;
import kr.baul.server.domain.order.orderinfo.OrderInfoMapper;
import kr.baul.server.domain.order.payment.PaymentProcessor;
import kr.baul.server.domain.order.usercoupon.CouponUseProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final ItemStockProcessor itemStockProcessor;
    private final OrderFactory orderFactory;
    private final PaymentProcessor paymentProcessor;
    private final OrderInfoMapper orderInfoMapper;
    private final OrderStore orderStore;
    private final CouponUseProcessor couponUseProcessor;


    public OrderInfo.Order registerOrder(OrderCommand.RegisterOrder registerOrder){
        var userId = registerOrder.userId();
        var requestItems = registerOrder.items();

        // 주문 상품 재고 차감
        itemStockProcessor.deductAllOrRollback(requestItems);

        Order order = null;
        try {
            // 주문 생성
            order = orderFactory.store(userId, requestItems);
        }catch (Exception e){
            // 재고 복구
            itemStockProcessor.revertAll(requestItems);
            throw e;
        }

        try {
            // 주문 금액 결제
            paymentProcessor.process(userId, order);

            // 결제된 금액에 맞게 상품에 적용된 쿠폰 사용으로 변경
            for (var item : requestItems) {
                if (item.couponId() != null) {
                    couponUseProcessor.use(item.couponId(), userId, order.getId());
                }
            }
        }catch (Exception e){
            // 재고 복구
            itemStockProcessor.revertAll(requestItems);

            // 쿠폰 사용 복구 : 이 주문에서 HOLD 된 쿠폰만 복구
            try {
                for (var item : requestItems) {
                    if(item.couponId() != null){
                        couponUseProcessor.cancelReserve(item.couponId(), userId, order.getId());
                    }
                }
            }catch (Exception ignore){}

            // 주문 상태 실패 처리
            order.setOrderStatus(Order.OrderStatus.FAILED);
            orderStore.store(order);
            throw e;
        }

        return orderInfoMapper.of(order);
    }

}
