package kr.baul.server.interfaces.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class AccountDto {

    @Getter
    @Setter
    public static class AccountChargeRequest{

        @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "userId 는 필수값입니다.")
        private Long userId;

        @Schema(description = "충전 금액", example = "10000", minimum = "1",
                maximum = "1000000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "amount 는 필수값입니다.")
        @Min(value = 1, message = "amount 는 1 이상이어야 합니다.")
        private Long amount;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class AccountChargeResponse{
        private Long balance;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class AccountBalanceResponse{
        private Long balance;
    }
}
