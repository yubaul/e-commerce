package kr.baul.server.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {

    private String code;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> CommonResponse<T> success(T data) {
        return (CommonResponse<T>) CommonResponse.builder()
                .code(ResponseCode.SUCCESS.getCode())
                .data(data)
                .message(ResponseCode.SUCCESS.getMessage())
                .build();
    }

    public static CommonResponse success(){
        return success(null);
    }

    public static CommonResponse fail(String code, String message) {
        return CommonResponse.builder()
                .code(code)
                .message(message)
                .build();
    }

    public static CommonResponse fail(ResponseCode errorCode) {
        return CommonResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }
}
