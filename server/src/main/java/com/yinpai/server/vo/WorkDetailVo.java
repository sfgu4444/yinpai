package com.yinpai.server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 3:15 下午
 */
@Data
@ApiModel("作品详情")
public class WorkDetailVo {

    @ApiModelProperty("是否免费")
    private boolean isFree;

    @ApiModelProperty("是否购买(免费作品为true)")
    private boolean isPay;

    @ApiModelProperty("作品ID")
    private Integer id;

    @ApiModelProperty("标题")
    public String title;

    @ApiModelProperty("正文")
    public String content;

    @ApiModelProperty("类型：1图片 2视频")
    public Integer type;

    @ApiModelProperty("商家ID")
    public Integer adminId;

    @ApiModelProperty("商家昵称")
    private String nickName;

    @ApiModelProperty("商家头像")
    private String avatarUrl;

    @ApiModelProperty("作品价格")
    public Integer price;

    @ApiModelProperty("是否关注")
    private boolean follow = false;

    @ApiModelProperty("是否收藏")
    private boolean collection = false;

    @ApiModelProperty("图片资源（type为1时显示）")
    private List<String> imagesResources;

    @ApiModelProperty("视频资源（type为2时显示）")
    private String videoResources;

    @ApiModelProperty("评论数")
    private Long commentCount;

    @ApiModelProperty("收藏数")
    private Long collectionCount;
}
