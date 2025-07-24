package kr.baul.server.domain.order;

import kr.baul.server.db.OrderDB;
import kr.baul.server.db.OrderItemDB;
import kr.baul.server.domain.coupon.Coupon;
import kr.baul.server.domain.coupon.CouponReader;
import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import kr.baul.server.domain.coupon.usercoupon.UserCouponReader;
import kr.baul.server.domain.item.Item;
import kr.baul.server.domain.item.ItemReader;
import kr.baul.server.domain.order.orderitem.OrderItem;
import kr.baul.server.domain.order.orderitem.OrderItemStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFactoryImpl implements OrderFactory {

    private final ItemReader itemReader;
    private final CouponReader couponReader;
    private final UserCouponReader userCouponReader;
    private final OrderStore orderStore;
    private final OrderItemStore orderItemStore;

    @Override
    public Order store(Long userId, List<OrderCommand.RegisterOrder.OrderItem> requestItems) {
        List<OrderItem> orderItems = new ArrayList<>();
        Long totalAmount = 0L;
        Order order = Order.builder()
                .id(OrderDB.getAtomicInteger())
                .userId(userId)
                .build();

        for (OrderCommand.RegisterOrder.OrderItem requestItem : requestItems) {
            Item item = itemReader.getItem(requestItem.itemId());

            int quantity = requestItem.quantity();
            Long itemPriceAtOrder = item.getPrice() * quantity;

            if (requestItem.couponId() != null) {
                UserCoupon userCoupon = userCouponReader.getUserCoupon(requestItem.couponId(), userId);
                userCoupon.use();;
                Coupon coupon = couponReader.getCoupon(userCoupon.getCouponId());
                itemPriceAtOrder = coupon.applyDiscount(itemPriceAtOrder);
            }

            OrderItem orderItem = OrderItem.builder()
                    .id(OrderItemDB.getAtomicInteger())
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
