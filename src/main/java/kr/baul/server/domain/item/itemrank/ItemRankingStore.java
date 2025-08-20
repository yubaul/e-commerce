package kr.baul.server.domain.item.itemrank;

import kr.baul.server.domain.order.OrderCommand;

import java.util.List;

public interface ItemRankingStore {
    void updateDailyRanking(List<OrderCommand.RegisterOrder.OrderItem> items);
}
