package kr.baul.server.infrastructure.item;

import static kr.baul.server.domain.order.QOrder.order;
import static kr.baul.server.domain.order.orderitem.QOrderItem.orderItem;
import static kr.baul.server.domain.item.QItem.item;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.baul.server.domain.item.iteminfo.ItemInfo;
import kr.baul.server.domain.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<ItemInfo.TopSelling> findTopSellingByPeriod(
            LocalDateTime start,
            LocalDateTime end,
            int limit
    ){
        return queryFactory.select(Projections.constructor(ItemInfo.TopSelling.class,
            item.id.as("itemId"),
            item.name.as("itemName"),
            orderItem.quantity.sum().as("salesVolume")
        ))
        .from(order)
        .join(orderItem).on(orderItem.orderId.eq(order.id))
        .join(item).on(item.id.eq(orderItem.itemId))
        .where(
                order.orderStatus.eq(Order.OrderStatus.PAID),
                order.createdAt.goe(start),
                order.createdAt.lt(end)
        )
        .groupBy(item.id)
        .orderBy(orderItem.quantity.sum().desc(), item.id.asc())
        .limit(limit)
        .fetch();

    }

}
