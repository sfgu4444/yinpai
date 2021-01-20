package com.yinpai.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Data
@ConfigurationProperties(prefix = "jsapi")
@Component
public class WechatJsApiConfig {

    private String opNotifyUrl;
    private String AppSecret;
    private String appid;
    private String EncodingAESKey;


    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
