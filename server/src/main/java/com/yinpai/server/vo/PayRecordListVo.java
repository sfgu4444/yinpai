package com.yinpai.server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/21 6:32 下午
 */
@Data
@ApiModel("打赏记录列表")
public class PayRecordListVo {

    @ApiModelProperty("商家昵称")
    private String nickName;

    @ApiModelProperty("类型1图2视频3vip")
    private Integer type;

    @ApiModelProperty("金额")
    private Integer money;

    @ApiModelProperty("时间")
    private Date createTime;
}
