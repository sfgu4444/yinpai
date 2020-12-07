package com.yinpai.server.domain.dto.fiter;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 2:28 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserCollectionFilterDto extends BaseFilterDto {

    private Integer userId;

    private Integer type;

    private Integer status;
}
