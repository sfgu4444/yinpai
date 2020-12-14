package com.yinpai.server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 2:27 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("首页列表返回")
public class IndexWorksVo {

    @ApiModelProperty("作品ID")
    private Integer id;

    @ApiModelProperty("标题")
    public String title;

    @ApiModelProperty("正文")
    public String content;

    @ApiModelProperty("封面图")
    public String coverImageUrl;

    @ApiModelProperty("类型：1图片 2视频")
    public Integer type;

    @ApiModelProperty("商家ID")
    public Integer adminId;

    @ApiModelProperty("商家昵称")
    private String nickName;

    @ApiModelProperty("商家头像")
    private String avatarUrl;

    @ApiModelProperty("是否关注")
    private boolean follow = false;

    @ApiModelProperty("发布时间")
    private Date createTime;

    @ApiModelProperty("浏览次数")
    private Long lookCount;

}
