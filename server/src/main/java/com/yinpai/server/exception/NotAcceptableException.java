package com.yinpai.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/17 4:20 下午
 */
@Getter
public class NotAcceptableException extends RuntimeException {

    private Integer code;

    public NotAcceptableException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public NotAcceptableException(String message) {
        super(message);
        this.code = HttpStatus.NOT_ACCEPTABLE.value();
    }
}
