package com.yinpai.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 未登陆异常
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/17 1:37 下午
 */
@Getter
public class NotLoginException extends RuntimeException{

    private Integer code;

    public NotLoginException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public NotLoginException(String message) {
        super(message);
        this.code = HttpStatus.UNAUTHORIZED.value();
    }
}
