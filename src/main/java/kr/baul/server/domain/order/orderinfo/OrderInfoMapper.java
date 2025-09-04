package kr.baul.server.domain.order.orderinfo;

import kr.baul.server.domain.order.Order;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OrderInfoMapper {
    OrderInfo.Order of(Order order);
    OrderInfo.OrderCompleted of(OrderInfo.Order orderInfo);
}
