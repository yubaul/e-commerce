package kr.baul.server.common.exception;

import kr.baul.server.common.response.ResponseCode;

public class DuplicateCouponIssueException extends BaseException{
    public DuplicateCouponIssueException() {
        super(ResponseCode.DUPLICATE_COUPON_ISSUE);
    }

}
