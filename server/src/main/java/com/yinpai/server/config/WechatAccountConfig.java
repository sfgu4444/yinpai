package com.yinpai.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "wechat")
@Component
public class WechatAccountConfig {

    private String opAppId;

    private String opMchId;

    private String opMchKey;

    private String opKeyPath;

    private String opNotifyUrl;
}
