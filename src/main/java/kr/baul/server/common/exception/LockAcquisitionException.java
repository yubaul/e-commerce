package kr.baul.server.common.exception;

import kr.baul.server.common.response.ResponseCode;

public class LockAcquisitionException extends BaseException{

    public LockAcquisitionException() {
        super(ResponseCode.LOCK_ACQUIRE_FAILED);
    }

    public LockAcquisitionException(String message) {
        super(message, ResponseCode.LOCK_ACQUIRE_FAILED);
    }
}
