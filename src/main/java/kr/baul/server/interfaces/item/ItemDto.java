package kr.baul.server.interfaces.item;

import kr.baul.server.application.item.ItemInfo;
import lombok.Builder;


public class ItemDto {

    @Builder
    public record ItemResponse(
            Item item
    ){
        @Builder
        public record Item(
                Long id,
                String name,
                Long price,
                int quantity
        ){ }
    }

}
