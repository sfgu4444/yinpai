package com.yinpai.server.domain.dto.fiter;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 2:28 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WorksFilterDto extends BaseFilterDto {

    private Integer adminId;

    private List<Integer> adminIds;

    private String keyword;

    private String title;

    private Integer type;

    private Integer status;
}
