package kr.baul.server.interfaces.item;

import kr.baul.server.application.item.ItemInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ItemDtoMapper {

    ItemDto.ItemResponse.Item of(ItemInfo info);

    default ItemDto.ItemResponse toResponse(ItemInfo info) {
        return ItemDto.ItemResponse.builder()
                .item(of(info))
                .build();
    }


}
