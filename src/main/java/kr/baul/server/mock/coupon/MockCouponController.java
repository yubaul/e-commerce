package kr.baul.server.mock.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.baul.server.common.response.CommonResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "MockCoupon", description = "쿠폰 Mock API (선착순 쿠폰 발급 / 보유 쿠폰 목록 조회)")
@RequestMapping("/api/v1/mock/coupon")
@RestController
public class MockCouponController {

    @Operation(summary = "선착순 쿠폰 발급 API", description = "userId를 통해 선착순 쿠폰을 발급합니다.")
    @ApiResponse(responseCode = "200", description = "선착순 쿠폰 발급 성공")
    @PostMapping("/issue")
    public CommonResponse issueCoupon(@RequestBody MockIssueCouponRequest request) {
        return CommonResponse.success(MockCoupon.dummy());
    }

    @Operation(summary = "보유 쿠폰 목록 조회 API", description = "userId를 통해 사용자의 보유 쿠폰 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "보유 쿠폰 목록 조회 성공")
    @GetMapping("/{userId}")
    public CommonResponse getCoupons(
            @Parameter(name = "userId", description = "사용자 ID", required = true, example = "1")
            @PathVariable Long userId
    ) {
        return CommonResponse.success(MockCouponList.dummy());
    }

    @Getter
    @Setter
    public static class MockIssueCouponRequest {

        @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long userId;
    }

    public record MockCoupon(Long id, String name, long discountAmount, LocalDate validFrom, LocalDate validTo, boolean isUsed) {
        public static MockCoupon dummy() {
            return new MockCoupon(
                    1L,
                    "수강 할인 쿠폰",
                    5_000L,
                    LocalDate.now(),
                    LocalDate.now().plusDays(7),
                    false
            );
        }
    }

    public record MockCouponList(List<MockCoupon> coupons) {
        public static MockCouponList dummy() {
            return new MockCouponList(List.of(
                    new MockCoupon(1L, "수강 할인 쿠폰", 5_000L, LocalDate.now(), LocalDate.now().plusDays(7), false),
                    new MockCoupon(2L, "콜라 할인 쿠폰", 3_000L, LocalDate.now().minusDays(1), LocalDate.now().plusDays(5), false)
            ));
        }
    }
}