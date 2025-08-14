package kr.baul.server.interfaces.item;

import lombok.Builder;

import java.util.List;


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


    @Builder
    public record TopSellingResponse(
            List<TopSelling> items
    ) {
        @Builder
        public record TopSelling(
                Long itemId,
                String itemName,
                long salesVolume
        ) { }
    }

}
