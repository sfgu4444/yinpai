package com.yinpai.server.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "alipay")
@Data
public class AlipayConfig {

    private String serverUrl;

    private String appId;

    private String alipayPublicKey;

    private String charset;

    private String appPrivateKey;

    private String signType;

    private String notifyUrl;

    private String timeoutExpress;

}
