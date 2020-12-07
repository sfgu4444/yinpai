package com.yinpai.server.service;

import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.yinpai.server.exception.NotAcceptableException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 11:40 上午
 */
@Service
public class SmsService {

    private final ConcurrentHashMap<String, Integer> codeMap = new ConcurrentHashMap<>();

    public void sendSms(String phone) {
        //生产环境请求地址：app.cloopen.com
        String serverIp = "app.cloopen.com";
        //请求端口
        String serverPort = "8883";
        //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
        String accountSId = "8aaf070874af41ee0174c8d376fc0a4f";
        String accountToken = "ea981f8ffe744d25956ac89a50a6f408";
        //请使用管理控制台中已创建应用的APPID
        String appId = "8aaf070874af41ee0174c8d377d70a55";
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_JSON);
        String to = phone;
        String templateId= "652960";
        Integer code = new Random().nextInt(9999);
        String[] datas = {String.valueOf(code), "10分钟"};
        HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
//        HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas,subAppend,reqId);
        if("000000".equals(result.get("statusCode"))){
            codeMap.put(phone, code);
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println(key +" = "+object);
            }
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
    }

    public void checkCode (String phone, Integer code) {
        Integer check = codeMap.get(phone);
        if (check == null || !check.equals(code)) {
            throw new NotAcceptableException("验证码不正确");
        }
        codeMap.remove(phone);
    }

}
