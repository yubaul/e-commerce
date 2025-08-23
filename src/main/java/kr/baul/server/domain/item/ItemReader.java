package kr.baul.server.domain.item;
import java.util.List;

public interface ItemReader {
    Item getItem(Long itemId);


    List<Item> getItems(List<Long> itemIds);
}