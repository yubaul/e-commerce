package kr.baul.server.application.item;

import kr.baul.server.domain.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ItemInfo {
    private Long id;
    private String name;
    private Long price;
    private int quantity;

    public ItemInfo(Item item){
        this.id = item.getId();
        this.name = item.getName();
        this.price = item.getPrice();
        this.quantity = item.getItemStock().getQuantity();
    }

}
