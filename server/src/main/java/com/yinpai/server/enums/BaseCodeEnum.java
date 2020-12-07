package com.yinpai.server.enums;

public interface BaseCodeEnum<T, D> {
    T getCode();

    D getMessage();
}
