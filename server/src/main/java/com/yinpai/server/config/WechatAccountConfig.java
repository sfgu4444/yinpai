package com.yinpai.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "wechat")
@Component
public class WechatAccountConfig {

    //应用ID true
    private String opAppId;

    //商户号 true
    private String opMchId;

    //签名加密时使用
    private String opMchKey;


    private String opKeyPath;

    //回调地址
    private String opNotifyUrl;
}
