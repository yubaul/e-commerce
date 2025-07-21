package kr.baul.server.common.exception;

import kr.baul.server.common.response.ResponseCode;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{

    private ResponseCode responseCode;

    public BaseException() {
    }

    public BaseException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }

    public BaseException(String message, ResponseCode responseCode) {
        super(message);
        this.responseCode = responseCode;
    }

}
