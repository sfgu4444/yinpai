package com.yinpai.server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/30 10:50 上午
 */
@Data
@ApiModel("商家可支付方式")
public class AdminPayMethodVo {

    @ApiModelProperty("VIP剩余到期时间(null或者小于当前时间则为0天)")
    private Date expireTime;

    @ApiModelProperty("月付价格（null或小于等于0为没开通此方式）")
    private Integer monthPayPrice;

    @ApiModelProperty("季付价格（null或小于等于0为没开通此方式）")
    private Integer quarterPayPrice;

    @ApiModelProperty("年付价格（null或小于等于0为没开通此方式）")
    private Integer yearPayPrice;
}
