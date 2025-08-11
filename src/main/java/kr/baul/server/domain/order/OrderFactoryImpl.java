package kr.baul.server.domain.order;

import kr.baul.server.common.exception.CouponAlreadyUsedException;
import kr.baul.server.domain.coupon.Coupon;
import kr.baul.server.domain.item.Item;
import kr.baul.server.domain.item.ItemReader;
import kr.baul.server.domain.order.orderitem.OrderItem;
import kr.baul.server.domain.order.orderitem.OrderItemStore;
import kr.baul.server.domain.order.usercoupon.CouponUseProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderFactoryImpl implements OrderFactory {

    private final ItemReader itemReader;
    private final OrderStore orderStore;
    private final OrderItemStore orderItemStore;
    private final CouponUseProcessor couponUseProcessor;

    @Override
    public Order store(Long userId, List<OrderCommand.RegisterOrder.OrderItem> requestItems) {
        List<OrderItem> orderItems = new ArrayList<>();
        Long totalAmount = 0L;
        Order order = Order.builder()
                .userId(userId)
                .build();

        for (OrderCommand.RegisterOrder.OrderItem requestItem : requestItems) {
            Item item = itemReader.getItem(requestItem.itemId());
            Long couponId = requestItem.couponId();;
            int quantity = requestItem.quantity();
            Long itemPriceAtOrder = item.getPrice() * quantity;

            if (couponId != null) {
                try {
                    Coupon coupon = couponUseProcessor.useCoupon(couponId, userId);
                    itemPriceAtOrder = coupon.applyDiscount(itemPriceAtOrder);
                }catch (CouponAlreadyUsedException e) {
                    log.warn("쿠폰 중복 사용: couponId={}, userId={}, msg={}", requestItem.couponId(), userId, e.getMessage());
                }
            }

            OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getId())
                    .itemId(item.getId())
                    .quantity(quantity)
                    .itemPriceAtOrder(itemPriceAtOrder)
                    .build();

            orderItems.add(orderItem);
            totalAmount += itemPriceAtOrder;
        }

        order.setTotalAmount(totalAmount);

        orderStore.store(order);
        orderItemStore.store(orderItems);

        return order;
    }
}
