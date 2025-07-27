package kr.baul.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class CouponDto {

    @Getter
    @Setter
    public static class CouponIssueRequest{
        @NotNull(message = "userId 는 필수값입니다.")
        @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long userId;

        @NotNull(message = "couponId 는 필수값입니다.")
        @Schema(description = "쿠폰 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long couponId;

    }


    @Getter
    @Builder
    public static class UserCoupon{
        private Long id;
        private Long userId;
        private Long couponId;
        private boolean used;
        private LocalDateTime usedAt;
        private LocalDateTime createdAt;
    }



}
