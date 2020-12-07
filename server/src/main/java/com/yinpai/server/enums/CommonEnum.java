package com.yinpai.server.enums;

import lombok.Getter;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2019-11-16 10:29
 */
@Getter
public enum CommonEnum implements BaseCodeEnum<Integer, String>{

    /**
     *
     */
    NO(0, "否"),
    YES(1, "是"),
    ;

    private Integer code;

    private String message;

    CommonEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
