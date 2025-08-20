package kr.baul.server.interfaces.coupon;

import kr.baul.server.domain.coupon.CouponCommand;
import kr.baul.server.domain.coupon.usercoupon.UserCouponDetail;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CouponDtoMapper {

    List<CouponDto.UserCoupon> of(List<UserCouponDetail.UserCouponInfo> userCouponInfos);

    CouponCommand.IssueCoupon of(CouponDto.CouponIssueRequest request);

}
