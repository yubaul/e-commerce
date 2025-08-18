package kr.baul.server.domain.order.itemstock;

import kr.baul.server.domain.item.*;
import kr.baul.server.domain.order.OrderCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemStockProcessorImpl implements ItemStockProcessor {
    private final ItemStockDeductionHandler handler;

    @Override
    public void deductAllOrRollback(List<OrderCommand.RegisterOrder.OrderItem> items) {
        // 교착상태(데드락) 예방을 위해 itemId 정렬
        var sorted = items.stream()
                .sorted(Comparator.comparing(OrderCommand.RegisterOrder.OrderItem::itemId))
                .toList();

        var done = new ArrayList<OrderCommand.RegisterOrder.OrderItem>(sorted.size());
        try {
            for (var item : sorted) {
                handler.deductOne(item);
                done.add(item);
            }
        } catch (Exception e) {
            // 역순 보상 트랜잭션
            for (int i = done.size() - 1; i >= 0; i--) {
                try { handler.revertOne(done.get(i)); } catch (Exception ignore) {}
            }
            throw e;
        }
    }

    @Override
    public void revertAll(List<OrderCommand.RegisterOrder.OrderItem> items) {
        // 교착상태(데드락) 예방을 위해 itemId 정렬
        var sorted = items.stream()
                .sorted(Comparator.comparing(OrderCommand.RegisterOrder.OrderItem::itemId))
                .toList();

        // 역순 보상 트랜잭션
        for (int i = sorted.size() - 1; i >= 0; i--) {
            try { handler.revertOne(sorted.get(i)); } catch (Exception ignore) {}
        }
    }

}
