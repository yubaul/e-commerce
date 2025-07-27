package kr.baul.server.interfaces.order;

import kr.baul.server.domain.order.OrderCommand;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OrderDtoMapper {


    OrderCommand.RegisterOrder toCommand(OrderDto.RegisterOrderRequest request);

    OrderCommand.RegisterOrder.OrderItem toCommand(OrderDto.RegisterOrderRequest.OrderItem item);

}
