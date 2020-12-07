package com.yinpai.server.domain.dto.fiter;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/5 10:56 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserFollowFilterDto extends BaseFilterDto  {

    private Integer userId;
}
