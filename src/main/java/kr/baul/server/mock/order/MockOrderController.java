package kr.baul.server.mock.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.baul.server.response.CommonResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "MockOrder", description = "주문/결제 Mock API")
@RestController
@RequestMapping("/api/v1/mock/orders")
public class MockOrderController {

    @Operation(summary = "주문/결제 API", description = "사용자 ID와 상품 ID/수량 목록으로 주문 및 결제를 수행합니다.")
    @ApiResponse(responseCode = "200", description = "주문 및 결제 성공")
    @PostMapping
    public CommonResponse order(@RequestBody MockOrderRequest request) {
        return CommonResponse.success(MockOrderResult.dummy());
    }

    @Getter
    @Setter
    public static class MockOrderRequest {

        @Schema(description = "사용자 ID", example = "1", required = true)
        private Long userId;

        @Schema(description = "주문할 상품 목록", required = true)
        private List<MockOrderItem> items;

        @Getter
        @Setter
        public static class MockOrderItem {

            @Schema(description = "상품 ID", example = "100", required = true)
            private Long itemId;

            @Schema(description = "구매 수량", example = "2", required = true)
            private Integer quantity;
        }
    }

    public record MockOrderResult(
            Long orderId,
            Long totalAmount,
            String status
    ) {
        public static MockOrderResult dummy() {
            return new MockOrderResult(99L, 158000L, "PAID");
        }
    }
}