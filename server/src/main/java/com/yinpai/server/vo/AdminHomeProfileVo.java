package com.yinpai.server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/30 11:09 上午
 */
@Data
@ApiModel("商家主页资料接口")
public class AdminHomeProfileVo {

    @ApiModelProperty("商家昵称")
    private String nickName;

    @ApiModelProperty("背景图")
    private String backgroundUrl;

    @ApiModelProperty("头像")
    private String avatarUrl;

    @ApiModelProperty("粉丝数")
    private Long fansNumber;

    @ApiModelProperty("是否加入vip")
    private boolean joinVip;

    @ApiModelProperty("是否关注")
    private boolean follow;
}
