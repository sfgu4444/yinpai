package com.yinpai.server.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "weixin.pay")
public class WeiXinPayProperties {
//     #关联的公众号appid
    private String appId;//: wxf913bfa3a2c7eeeb
//    #商户号
    private String partner;//: 1543338551
//            #商户key
    private String partnerKey;//: atguigu3b0kn9g5v426MKfHQH7X8rKwb
//    #回调地址
    private String notifyUrl;//: #用户支付后微信发送支付信息的回调地址
}
