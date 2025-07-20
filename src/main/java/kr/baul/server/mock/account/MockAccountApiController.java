package kr.baul.server.mock.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.baul.server.common.response.CommonResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MockAccount", description = "계좌 Mock API (잔액 충전 / 조회)")
@RequestMapping("/api/v1/mock/accounts")
@RestController
public class MockAccountApiController {

    @Operation(summary = "계좌 잔액 충전 API", description = "userId를 통해 계좌에 금액을 충전합니다.")
    @ApiResponse(responseCode = "200", description = "잔액 충전 성공")
    @PostMapping("/charge")
    public CommonResponse charge(@RequestBody MockAccountChargeRequest request){
        var result = MockChargeBalance.dummy();
        return CommonResponse.success(result);
    }

    @Getter
    @Setter
    public static class MockAccountChargeRequest {

        @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long userId;

        @Schema(description = "충전 금액", example = "10000", minimum = "1",
                maximum = "1000000", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long amount;
    }

    public record MockChargeBalance(Long balance){
        public static MockChargeBalance dummy(){
            return new MockChargeBalance(20_000L);
        }
    }

    @Operation(summary = "계좌 잔액 조회 API", description = "userId를 통해 사용자 계좌 잔액을 조회한다.")
    @GetMapping("/{userId}")
    @ApiResponse(responseCode = "200", description = "계좌 잔액 조회 성공")
    public CommonResponse retrieveAccount(
            @Parameter(
                    name = "userId",
                    description = "사용자 ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long userId
    ){
        var result = MockAccount.dummy();
        return CommonResponse.success(result);
    }


    public record MockAccount(Long balance){
        public static MockAccount dummy(){
            return new MockAccount(10_000L);
        }

    }


}
