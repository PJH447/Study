package com.demo.lucky_platform.web.common.dto;

import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

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

    public static <T> CommonResponse<T> createErrorResponse(Throwable e) {
        return CommonResponse.<T>builder()
                             .success(false)
                             .message(e.getMessage())
                             .type(ResponseType.ERROR)
                             .build();
    }

    public static <T> CommonResponse<T> createErrorResponse(String errorMessage) {
        return CommonResponse.<T>builder()
                             .success(false)
                             .message(errorMessage)
                             .type(ResponseType.ERROR)
                             .build();
    }

    public static <T> CommonResponse<T> createVoidResponse() {
        return CommonResponse.<T>builder()
                             .success(true)
                             .message("success")
                             .type(ResponseType.NO_CONTENT)
                             .build();
    }

    public static <T> CommonResponse<T> createResponse(T data) {
        return CommonResponse.<T>builder()
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
        SLICE,
        PAGE,
        NO_CONTENT;

        public static <T> ResponseType getResponseType(T data) {
            if (data instanceof List) {
                return LIST;
            } else if (data instanceof Page) {
                return PAGE;
            } else if (data instanceof Slice<?>) {
                return SLICE;
            } else {
                return SINGLE;
            }
        }
    }
}
