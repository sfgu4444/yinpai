package com.yinpai.server.enums;

import lombok.Getter;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/19 3:32 下午
 */
@Getter
public enum PageRequestEnum implements BaseCodeEnum<String, String>{

    /**
     *
     */
    PAGE("page", "页数"),
    SIZE("size", "页码"),
    sortDirection("sortDirection", "排序方式"),
    sortDirectionField("sortDirectionField", "排序字段");
    ;

    private String code;

    private String message;

    PageRequestEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
