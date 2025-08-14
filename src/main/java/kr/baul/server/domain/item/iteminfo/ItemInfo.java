package kr.baul.server.domain.item.iteminfo;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
public class ItemInfo {

    @Builder
    public record Item(
            Long id,
            String name,
            Long price,
            int quantity
    ){

    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    public static class TopSelling{
        private Long itemId;
        private String itemName;
        private Integer salesVolume;
    }

}
