package kr.baul.server.domain.coupon;

import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserCouponDetailMapper {

    List<UserCouponDetail.UserCouponInfo> of(List<UserCoupon> userCoupons);
}
