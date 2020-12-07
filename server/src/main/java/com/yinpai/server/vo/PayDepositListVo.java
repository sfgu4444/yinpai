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
@ApiModel("充值记录列表")
public class PayDepositListVo {

    @ApiModelProperty("支付金额")
    private Integer payMoney;

    @ApiModelProperty("获得币数")
    private Integer userMoney;

    @ApiModelProperty("时间")
    private Date createTime;
}
