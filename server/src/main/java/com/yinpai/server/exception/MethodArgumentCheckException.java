package com.yinpai.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 参数检验失败
 * @author weilai
 */
@Getter
public class MethodArgumentCheckException extends RuntimeException {

    private Integer code;

    public MethodArgumentCheckException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public MethodArgumentCheckException(String message) {
        super(message);
        this.code = HttpStatus.BAD_REQUEST.value();
    }
}
