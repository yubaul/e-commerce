package kr.baul.server.common.exception;

import kr.baul.server.common.response.ResponseCode;

public class PaymentFailedException extends BaseException{

    public PaymentFailedException(){
        super(ResponseCode.PAYMENT_FAILED);
    }
}
