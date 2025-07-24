package kr.baul.server.db;

import jakarta.annotation.PostConstruct;
import kr.baul.server.domain.item.Item;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class ItemDB {

    private final Map<Long, Item> table = new HashMap<>();

    @PostConstruct
    public void init(){
        Item item = Item.builder()
                .id(1L)
                .name("질레트 면도기")
                .price(23_000L)
                .quantity(10)
                .build();

        table.put(item.getId(), item);
    }

    public Item insert(Item item){
        throttle(300);
        table.put(item.getId(), item);
        return item;
    }

    public Optional<Item> selectById(Long itemId) {
        throttle(200);
        return Optional.ofNullable(table.get(itemId));
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {

        }
    }
}
