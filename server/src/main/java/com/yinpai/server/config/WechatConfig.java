package com.yinpai.server.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WechatConfig {

    private final WechatAccountConfig wechatAccountConfig;

    @Autowired
    public WechatConfig(WechatAccountConfig wechatAccountConfig) {
        this.wechatAccountConfig = wechatAccountConfig;
    }

    public WxPayConfig appPayConfig() {
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setAppId(wechatAccountConfig.getOpAppId());
        wxPayConfig.setMchId(wechatAccountConfig.getOpMchId());
        wxPayConfig.setMchKey(wechatAccountConfig.getOpMchKey());
        wxPayConfig.setKeyPath(wechatAccountConfig.getOpKeyPath());
        wxPayConfig.setNotifyUrl(wechatAccountConfig.getOpNotifyUrl());
        return wxPayConfig;
    }
}
