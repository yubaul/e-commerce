package kr.baul.server.common.exception;

import kr.baul.server.common.response.ResponseCode;

public class CouponAlreadyUsedException extends BaseException{

    public CouponAlreadyUsedException(){
        super(ResponseCode.COUPON_ALREADY_USED);
    }

    public CouponAlreadyUsedException(String message){
        super(message, ResponseCode.COUPON_ALREADY_USED);
    }
}
