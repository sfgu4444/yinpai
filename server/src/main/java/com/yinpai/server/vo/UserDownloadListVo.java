package com.yinpai.server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/6 6:03 下午
 */
@Data
@ApiModel("用户下载列表")
public class UserDownloadListVo {

    @ApiModelProperty("作品ID")
    private Integer workId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("类型：1图片2视频")
    private Integer type;
}
