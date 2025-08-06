package kr.baul.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.baul.server.application.coupon.CouponService;
import kr.baul.server.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Coupon API", description = "쿠폰 API (선착순 쿠폰 발급 / 보유 쿠폰 목록 조회)")
@RequestMapping("/api/v1/coupon")
@RestController
@RequiredArgsConstructor
public class CouponApiController {

    private final CouponService couponService;
    private final CouponDtoMapper couponDtoMapper;

    @Operation(summary = "선착순 쿠폰 발급 API", description = "userId를 통해 선착순 쿠폰을 발급합니다.")
    @PostMapping("/issue")
    public CommonResponse issueCouponToUser(@RequestBody @Valid CouponDto.CouponIssueRequest request) {
        var command = couponDtoMapper.of(request);
        couponService.issueCouponToUser(command);
        return CommonResponse.success();
    }

    @Operation(summary = "보유 쿠폰 목록 조회 API", description = "userId를 통해 사용자의 보유 쿠폰 목록을 조회합니다.")
    @GetMapping("/{userId}")
    public CommonResponse<List<CouponDto.UserCoupon>> getUserCoupons(
            @Parameter(name = "userId", description = "사용자 ID", required = true, example = "1")
            @PathVariable Long userId
    ) {
        var result = couponService.getUserCoupons(userId);
        var response = couponDtoMapper.of(result);
        return CommonResponse.success(response);
    }
}
