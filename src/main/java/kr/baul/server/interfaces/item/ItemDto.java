package kr.baul.server.interfaces.item;

import kr.baul.server.application.item.ItemInfo;


public class ItemDto {


    public record ItemResponse(
            Item item
    ){
        public static ItemResponse createResponse(ItemInfo itemInfo){
            return new ItemResponse(Item.createItem(itemInfo));
        }

        public record Item(
                Long id,
                String name,
                Long price,
                int quantity
        ){
            private static Item createItem(ItemInfo itemInfo){
                return new Item(
                        itemInfo.getId(),
                        itemInfo.getName(),
                        itemInfo.getPrice(),
                        itemInfo.getQuantity()
                );
            }
        }
    }

}
