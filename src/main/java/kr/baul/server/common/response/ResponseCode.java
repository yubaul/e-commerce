package kr.baul.server.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode{

    SUCCESS("00000", "요청이 성공적으로 처리되었습니다."),

    // 90000 번 부터 BaseException Error (예상 가능한 예외) 기존에는 응답코드와 에러코드가 분리되어 있었지만,
    // 현재는 응답코드가 하나여서 하나로 합쳤음.
    // 추후 에러 코드가 많이지면 충분히 ErrorCode 로 분리 가능
    SYSTEM_ERROR("90000","일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."), //서버 장애 상황
    INVALID_PARAMETER("90001", "요청한 값이 올바르지 않습니다."),
    DUPLICATE_COUPON_ISSUE("90002", "이미 발급받은 쿠폰입니다."),
    OUT_OF_STOCK("90003", "재고가 부족합니다."),
    COUPON_ALREADY_USED("90004", "이미 사용된 쿠폰입니다"),
    COUPON_DISABLED("90005", "사용이 중지된 쿠폰입니다."),
    INSUFFICIENT_BALANCE("90006", "계좌 잔액이 부족합니다.");

    private final String code;

    private final String message;

    public String getMessage(Object... arg) {
        return String.format(message, arg);
    }

}
