package kr.baul.server.domain.item.iteminfo;

import kr.baul.server.domain.item.Item;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ItemInfoMapper {

    @Mapping(
            target = "quantity",
            expression = "java(item.getItemStock() != null ? item.getItemStock().getQuantity() : 0)"
    )
    ItemInfo.Item of(Item item);

}
