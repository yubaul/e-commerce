package kr.baul.server.interfaces.account;

import static kr.baul.server.interfaces.account.AccountDto.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.baul.server.application.account.AccountService;
import kr.baul.server.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Account API", description = "계좌 API (잔액 충전 / 조회)" )
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountApiController {

    private final AccountService accountService;
    private final AccountDtoMapper accountDtoMapper;

    @Operation(summary = "계좌 잔액 충전 API", description = "userId를 통해 계좌에 금액을 충전합니다.")
    @PostMapping("/charge")
    public CommonResponse<AccountDto.AccountChargeResponse> charge(@RequestBody @Valid AccountChargeRequest request){
        var command = accountDtoMapper.of(request);
        var result = accountService.charge(command);
        var response = new AccountChargeResponse(result);
        return CommonResponse.success(response);
    }


    @Operation(summary = "계좌 잔액 조회 API", description = "userId를 통해 사용자 계좌 잔액을 조회한다.")
    @GetMapping("/{userId}")
    public CommonResponse<AccountDto.AccountBalanceResponse> getAccountBalance(
            @Parameter(
                    name = "userId",
                    description = "사용자 ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long userId
    ){
        var result = accountService.getAccountBalance(userId);
        var response = new AccountBalanceResponse(result);
        return CommonResponse.success(response);
    }
}
