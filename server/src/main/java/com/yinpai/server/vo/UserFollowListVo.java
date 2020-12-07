package com.yinpai.server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/5 10:54 下午
 */
@Data
@ApiModel("用户关注列表")
public class UserFollowListVo {

    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("商家ID")
    private Integer adminId;

    @ApiModelProperty("头像")
    private String avatarUrl;

    @ApiModelProperty("作品更新次数")
    private Long updateCount;
}
