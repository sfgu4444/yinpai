package com.yinpai.server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/5 10:18 下午
 */
@Data
@ApiModel("用户资料详情")
public class UserProfileVo {

    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("头像")
    private String avatarUrl;

    @ApiModelProperty("背景图")
    private String backgroundUrl;

    @ApiModelProperty("性别")
    private Integer gender;

    @ApiModelProperty("地区")
    private String area;

    @ApiModelProperty("个性签名")
    private String personalSignature;
}
