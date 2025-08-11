package kr.baul.server.interfaces.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.baul.server.application.order.OrderFacade;
import kr.baul.server.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order API", description = "주문/결제 Mock API")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderFacade orderFacade;
    private final OrderDtoMapper orderDtoMapper;


    @Operation(summary = "주문/결제 API", description = "사용자 ID와 상품 ID/수량 목록으로 주문 및 결제를 수행합니다.")
    @PostMapping
    public CommonResponse order(@RequestBody @Valid OrderDto.RegisterOrderRequest request) {
        var command = orderDtoMapper.of(request);
        orderFacade.registerOrder(command);
        return CommonResponse.success();
    }
}
