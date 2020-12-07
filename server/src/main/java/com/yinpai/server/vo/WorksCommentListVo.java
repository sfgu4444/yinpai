package com.yinpai.server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/30 9:48 下午
 */
@Data
@ApiModel("评论")
public class WorksCommentListVo {

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("头像")
    private String avatarUrl;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("作者ID")
    private Integer adminId;

    @ApiModelProperty("商家昵称")
    private String adminNickName;

    @ApiModelProperty("商家头像")
    private String adminAvatarUrl;

    @ApiModelProperty("商家回复内容")
    private String replyContent;

    @ApiModelProperty("评论时间")
    private Date createTime;

    @ApiModelProperty("回复时间")
    private Date replyTime;
}
