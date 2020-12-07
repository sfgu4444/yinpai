package com.yinpai.server.vo.admin;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResultVO<T> implements Serializable {

    private static final long serialVersionUID = 2721046357945700438L;

    //    错误吗
    private Integer code;

    //    提示信息
    private String msg = "";

    //    具体数据
    private T data;
}
