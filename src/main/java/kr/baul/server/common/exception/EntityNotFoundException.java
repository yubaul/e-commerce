package kr.baul.server.common.exception;

import kr.baul.server.common.response.ResponseCode;

public class EntityNotFoundException extends BaseException{

    public EntityNotFoundException() {
        super(ResponseCode.INVALID_PARAMETER);
    }

    public EntityNotFoundException(String message){
        super(message, ResponseCode.INVALID_PARAMETER);
    }
}
