package com.yinpai.server.controller.app;

import com.google.gson.Gson;
import com.yinpai.server.log.WebLog;
import com.yinpai.server.service.UserPayRecordService;
import com.yinpai.server.vo.WxPay.PayResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author jialeiniu
 * @email 15601286367@163.com
 * @date 2020-12-8 15:31:19
 */
@RestController
@Api(tags = "供支付平台调用")
@RequestMapping("/mall/pay")
public class UserPayResultController {

    @Autowired
    private UserPayRecordService userPayRecordService;

    @ApiOperation("供微信平台回调")
    @PostMapping("/callbackWxAppPay")
    //@WebLog(description = "供微信平台回调")
    public String wxAppPayResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("【微信平台回调】");
        return userPayRecordService.wxAppPayResult(request,response);
    }

    @ApiOperation("供支付宝平台回调")
    @PostMapping("/callbackAlipayApp")
    @WebLog(description = "供支付宝平台回调")
    public String AliPayAppPayResult(Map<String,String>map){
        return userPayRecordService.AliPayAppPayResult(map);
    }

    /**
     * IOS做支付
     * 支付完成后  数据发给后端
     * 后端拿数据去苹果查询订单结果
     * 结果存入数据库
     * 然后给前端返回成功或失败
     *
     * @param map
     * @return
     */
    @ApiOperation("供苹果确认订单")
    @PostMapping("/appleOrderDetermine")
    @WebLog(description = "供苹果确认订单")
    public PayResultVo appleOrderDetermine(@RequestBody Map<String,Map<String,Object>> map) throws JSONException {
        System.out.println(new Gson().toJson(map.get("map")));
        return userPayRecordService.appleOrderDetermine(map.get("map"));
    }


}