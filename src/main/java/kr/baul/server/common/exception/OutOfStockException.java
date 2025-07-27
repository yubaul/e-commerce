package kr.baul.server.common.exception;

import kr.baul.server.common.response.ResponseCode;

public class OutOfStockException extends BaseException{

    public OutOfStockException() {
        super(ResponseCode.OUT_OF_STOCK);
    }

    public OutOfStockException(String message){
        super(message,ResponseCode.OUT_OF_STOCK);
    }
}
