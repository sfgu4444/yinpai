package com.yinpai.server.domain.dto.fiter;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 2:28 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WorksCommentFilterDto extends BaseFilterDto {

    private Integer workId;

    private String content;

    private Set<Integer> workIds;

}
