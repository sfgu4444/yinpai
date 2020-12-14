package com.yinpai.server.vo.WxPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jialeiniu
 * @email 15601286367@163.com
 * @date 2020-12-8 15:37:49
 */
@Data
@ApiModel("微信充值结果")
public class WxPayResult {

    @ApiModelProperty("小程序ID")
    private String appid;

    @ApiModelProperty("商家数据包")
    private String attach;

    @ApiModelProperty("付款银行")
    private String bank_type;

    @ApiModelProperty("货币种类")
    private String fee_type;

    @ApiModelProperty("是否关注公众账号")
    private String is_subscribe;

    @ApiModelProperty("商户号")
    private String mch_id;

    @ApiModelProperty("随机字符串")
    private String nonce_str;

    @ApiModelProperty("用户标识")
    private String openid;

    @ApiModelProperty("商户订单号")
    private String out_trade_no;

    @ApiModelProperty("业务结果 SUCCESS/FAIL")
    private String result_code;

    @ApiModelProperty("为SUCCESS时,其它字段才有值")
    private String return_code;

    @ApiModelProperty("签名")
    private String sign;

    @ApiModelProperty("支付完成时间")
    private String time_end;

    @ApiModelProperty("订单金额")
    private String total_fee;

    @ApiModelProperty("单个代金券支付金额")
    private String coupon_fee_$n;

    @ApiModelProperty("代金券使用数量")
    private String coupon_count;

    @ApiModelProperty("代金券类型")
    private String coupon_type_$n;

    @ApiModelProperty("代金券ID")
    private String coupon_id_$n;

    @ApiModelProperty("交易类型")
    private String trade_type;

    @ApiModelProperty("微信支付订单号")
    private String transaction_id;
}
