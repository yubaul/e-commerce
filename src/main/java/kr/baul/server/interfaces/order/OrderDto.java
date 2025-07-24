package kr.baul.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class OrderDto {

    @Getter
    @Setter
    public static class RegisterOrderRequest{

        @NotNull(message = "사용자 ID 는 필수값입니다.")
        @Schema(description = "사용자 ID", example = "1", required = true)
        private Long userId;

        @NotNull(message = "items 는 필수값입니다.")
        @Schema(description = "주문할 상품 목록", required = true)
        private List<OrderItem> items;

        @Getter
        @Setter
        public static class OrderItem {

            @NotNull(message = "itemId 는 필수값입니다.")
            @Schema(description = "상품 ID", example = "1", required = true)
            private Long itemId;

            @NotNull(message = "quantity 는 필수값입니다.")
            @Min(value = 1, message = "quantity 는 1 이상이어야 합니다.")
            @Schema(description = "구매 수량", example = "2", required = true)
            private Integer quantity;

            @Schema(description = "사용 쿠폰", example = "1")
            private Long couponId;
        }

    }
}
