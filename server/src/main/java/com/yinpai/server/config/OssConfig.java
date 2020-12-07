package com.yinpai.server.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description: oss配置
 * @author: yangshumin
 * @create: 2019-06-24 17:22
 */
@Slf4j
@Data
@Component
public class OssConfig {

    @Value("${oss.access.keyId}")
    private String accessKeyId;

    @Value("${oss.access.keySecret}")
    private String accessKeySecret;

    @Value("${oss.bucketName}")
    private String bucketName;

    /**
     * 外网访问域名
     */
    @Value("${oss.endpoint.outer}")
    private String endpointOuter;
    /**
     * 内网访问域名
     */
    @Value("${oss.endpoint.inner}")
    private String endpointInner;


}
