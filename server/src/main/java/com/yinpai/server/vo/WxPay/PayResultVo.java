package com.yinpai.server.vo.WxPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@ApiModel("支付结果")
public class PayResultVo {

    @ApiModelProperty("响应状态码")
    private HttpStatus code;

    @ApiModelProperty("消息")
    private String msg;

    @ApiModelProperty("响应正文")
    private Object data;

}
