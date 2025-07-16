package kr.baul.server.mock.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MockAccountChargeRequest {

    @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "충전 금액", example = "10000", minimum = "1",
            maximum = "1000000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long amount;
}
