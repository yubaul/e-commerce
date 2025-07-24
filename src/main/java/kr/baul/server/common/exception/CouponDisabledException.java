package kr.baul.server.common.exception;

import kr.baul.server.common.response.ResponseCode;

public class CouponDisabledException extends BaseException{

    public CouponDisabledException(){
        super(ResponseCode.COUPON_DISABLED);
    }

    public CouponDisabledException(String message){
        super(message, ResponseCode.COUPON_DISABLED);
    }

}
