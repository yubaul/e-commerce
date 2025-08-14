package kr.baul.server.interfaces.item;

import kr.baul.server.domain.item.iteminfo.ItemInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ItemDtoMapper {

    ItemDto.ItemResponse.Item of(ItemInfo.Item info);

    default ItemDto.ItemResponse toResponse(ItemInfo.Item item) {
        return ItemDto.ItemResponse.builder()
                .item(of(item))
                .build();
    }

    ItemDto.TopSellingResponse.TopSelling toDto(ItemInfo.TopSelling info);

    List<ItemDto.TopSellingResponse.TopSelling> toDtoList(List<ItemInfo.TopSelling> infos);

    default ItemDto.TopSellingResponse toTopSellingResponse(List<ItemInfo.TopSelling> infos) {
        return ItemDto.TopSellingResponse.builder()
                .items(toDtoList(infos))
                .build();
    }

}
