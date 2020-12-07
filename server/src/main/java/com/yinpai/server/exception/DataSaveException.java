package com.yinpai.server.exception;

import lombok.Getter;

/**
 * 数据更新异常
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/17 12:25 下午
 */
@Getter
public class DataSaveException extends RuntimeException {

    private Integer code;

    public DataSaveException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public DataSaveException(String message) {
        super(message);
        this.code = 500;
    }
}
