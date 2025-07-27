package kr.baul.server.common.exception;

import kr.baul.server.common.response.ResponseCode;

public class InsufficientBalanceException extends BaseException{

    public InsufficientBalanceException(){
        super(ResponseCode.INSUFFICIENT_BALANCE);
    }

}
