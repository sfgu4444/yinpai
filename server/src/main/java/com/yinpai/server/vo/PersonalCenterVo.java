package com.yinpai.server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/5 10:25 下午
 */
@Data
@ApiModel("个人中心数据")
public class PersonalCenterVo {

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

    @ApiModelProperty("签名")
    private String personalSignature;

    @ApiModelProperty("余额")
    private Integer money;

    @ApiModelProperty("关注")
    private Long followCount;

    @ApiModelProperty("收藏")
    private Long collectionCount;

    @ApiModelProperty("手机号")
    private String phone;

}
