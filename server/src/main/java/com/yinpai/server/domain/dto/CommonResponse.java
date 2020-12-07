package com.yinpai.server.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2019-06-21 19:38
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonResponse<T> implements Serializable {

    private static final long serialVersionUID = -5332037387083810485L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 提示信息
     */
    @Builder.Default
    private String msg = "";

    /**
     * 具体数据
     */
    private T data;

}
