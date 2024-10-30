package com.demo.lucky_platform.web.common.dto;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {

    private Boolean success;
    private String message;
    private ResponseType type;
    private T data;

    public static CommonResponse createErrorResponse(Throwable e) {
        return CommonResponse.builder()
                             .success(false)
                             .message(e.getMessage())
                             .type(ResponseType.ERROR)
                             .build();
    }

    public static <T> CommonResponse<T> createVoidResponse() {
        return (CommonResponse<T>) CommonResponse.builder()
                                                 .success(true)
                                                 .message("success")
                                                 .type(ResponseType.NO_CONTENT)
                                                 .build();
    }

    public static <T> CommonResponse<T> createResponse(T data) {
        return (CommonResponse<T>) CommonResponse.builder()
                                                 .success(true)
                                                 .message("success")
                                                 .type(ResponseType.getResponseType(data))
                                                 .data(data)
                                                 .build();
    }

    public enum ResponseType {
        ERROR,
        SINGLE,
        LIST,
        PAGE,
        NO_CONTENT
        ;

        public static <T> ResponseType getResponseType(T data) {
            if (data instanceof List) {
                return LIST;
            } else if (data instanceof Page) {
                return PAGE;
            } else {
                return SINGLE;
            }
        }
    }
}
