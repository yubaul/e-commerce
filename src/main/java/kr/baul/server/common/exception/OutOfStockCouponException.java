package kr.baul.server.common.exception;

import kr.baul.server.common.response.ResponseCode;

public class OutOfStockCouponException extends BaseException{

    public OutOfStockCouponException() {
        super(ResponseCode.OUT_OF_STOCK_COUPON);
    }
}
