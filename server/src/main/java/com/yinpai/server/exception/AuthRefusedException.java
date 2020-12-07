package com.yinpai.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/17 1:48 下午
 */
@Getter
public class AuthRefusedException extends RuntimeException {

    private Integer code;

    public AuthRefusedException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public AuthRefusedException(String message) {
        super(message);
        this.code = HttpStatus.FORBIDDEN.value();
    }
}
