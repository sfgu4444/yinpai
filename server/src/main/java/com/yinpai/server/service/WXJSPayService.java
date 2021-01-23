package com.yinpai.server.service;

import com.yinpai.server.log.WebLog;
import io.swagger.annotations.ApiOperation;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class WXJSPayService {


    // todo userpayservice wechatAuth 方法  下面是改动


     // todo controller
//    @ApiOperation("微信公众号认证")
//    @PostMapping("/wechat/auth")
//    @WebLog(description = "微信公众号认证")
//    public Map<String, Object> wechatAuth(String code, Integer totalFee, HttpServletRequest request) throws IOException {
//        return userPayService.wechatAuth(code,totalFee,request);
//    }


     // todo service
//    public Map<String, Object> wechatAuth(String code,Integer totalFee,HttpServletRequest request) throws IOException {
//        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
//        if (userInfoDto == null) {
//            throw new NotLoginException("请先登陆");
//        }
//        String format = MessageFormat.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid={0}&secret={1}&code={2}&grant_type=authorization_code",
//                "wx96095d1c2acffb94","83888971018c3054bd1ad72f099edea8", code);
//        String s = restTemplate.getForObject(format, String.class);
//        log.info("【openid 返回结果 】: {}",new Gson().toJson(s));
//        Map<String,String> codeMap = JsonUtils.toObject(s, Map.class);
//        String body = "yinpai";            // 商家自己随便写的消息
//        String out_trade_no = "1234567890";  // 订单ID
//        totalFee = totalFee*100; // 金额转成分  // 金额
//        //String userIp = "111.204.59.194"; // 用户IP地址
//        String userIp = getIpAddr(request); // 用户IP地址
//        String openId = codeMap.get("openid");  // code 请求返回的ID
//        String s1 = unifiedOrder(body,out_trade_no,totalFee,userIp,openId); // 获取预支付结果
//        log.info("【预支付返回结果 】: {}",new Gson().toJson(s1));
//        return getPayMap(s1); // 解析xml 返回 map
//    }

    // ----------------------------------- 调用 统一 公众号统一下单 -----------------------------------
    /**
     * 统一下单
     * @param body
     * @param out_trade_no
     * @param total_fee
     * @param IP
     * @param
     * @param openid
     * @return
     * @throws IOException
     */
    public static String unifiedOrder(String body,String out_trade_no,Integer total_fee,String IP,String openid)throws IOException {
        //设置访问路径
        HttpPost httppost = new HttpPost("https://api.mch.weixin.qq.com/pay/unifiedorder");
        String nonce_str = getNonceStr().toUpperCase();//随机
        String sign = "appid=" + "wx96095d1c2acffb94"+  // 公众号ID
                "&body=" + body +            // 描述
                "&mch_id=" + "1604725045" +   // 商户ID
                "&nonce_str=" + nonce_str +          //  随机三十二位
                "&notify_url=" + "https://ypapi.phpisfuture.com/mall/pay/callbackjsApipayApp" +  // 回调地址
                "&openid=" + openid +                       // code 获取
                "&out_trade_no=" + out_trade_no +           // 商户订单号
                "&spbill_create_ip=" + IP +              // 用户IP
                "&total_fee=" + "1" + // 单位分 todo 测试用 1 分钱
                "&trade_type=" + "JSAPI" + // 支付方式
                "&key=" + "bc56e221598fc34298c8af55f2cafff7"; //这个字段是用于之后MD5加密的，字段要按照ascii码顺序排序
        sign = MD5(sign).toUpperCase();

        //组装包含openid用于请求统一下单返回结果的XML
        StringBuilder sb = new StringBuilder("");
        sb.append("<xml>");
        setXmlKV(sb,"appid","wx96095d1c2acffb94");
        setXmlKV(sb,"body",body);
        setXmlKV(sb,"mch_id","1604725045");  // 商户ID
        setXmlKV(sb,"nonce_str",nonce_str);
        setXmlKV(sb,"notify_url","https://ypapi.phpisfuture.com/mall/pay/callbackjsApipayApp");  // 回调地址
        setXmlKV(sb,"openid",openid);              // code 获取
        setXmlKV(sb,"out_trade_no",out_trade_no);   // 商户订单号
        setXmlKV(sb,"spbill_create_ip",IP); // 用户ip
        setXmlKV(sb,"total_fee","1"); // 单位分
        setXmlKV(sb,"trade_type","JSAPI");  // 支付类型
        setXmlKV(sb,"sign",sign);
        sb.append("</xml>");
        System.out.println("统一下单请求：" + sb);

        StringEntity reqEntity = new StringEntity(new String (sb.toString().getBytes("UTF-8"),"ISO8859-1"));//这个处理是为了防止传中文的时候出现签名错误
        httppost.setEntity(reqEntity);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httppost);
        String strResult = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));

        System.out.println("统一下单返回xml：" + strResult);

        return strResult;
    }

    /**
     * 获取32位随机字符串
     * @return
     */
    public static String getNonceStr(){
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rd = new Random();
        for(int i = 0 ; i < 32 ; i ++ ){
            sb.append(str.charAt(rd.nextInt(str.length())));
        }
        return sb.toString();
    }

    /**
     * 插入XML标签
     * @param sb
     * @param Key
     * @param value
     * @return
     */
    public static StringBuilder setXmlKV(StringBuilder sb,String Key,String value){
        sb.append("<");
        sb.append(Key);
        sb.append(">");

        sb.append(value);

        sb.append("</");
        sb.append(Key);
        sb.append(">");

        return sb;
    }
    /**
     * 根据统一下单返回预支付订单的id和其他信息生成签名并拼装为map（调用微信支付）
     * @param prePayInfoXml
     * @return
     */
    public static Map<String,Object> getPayMap(String prePayInfoXml){
        Map<String,Object> map = new HashMap<String,Object>();
        String prepay_id = getXmlPara(prePayInfoXml,"prepay_id");//统一下单返回xml中prepay_id
        String timeStamp = String.valueOf((System.currentTimeMillis()/1000));//1970年到现在的秒数
        String nonceStr = getNonceStr().toUpperCase();//随机数据字符串
        String packageStr = "prepay_id=" + prepay_id;
        String signType = "MD5";
        String paySign =
                "appId=" + "wx96095d1c2acffb94" +
                        "&nonceStr=" + nonceStr +
                        "&package=prepay_id=" + prepay_id +
                        "&signType=" + signType +
                        "&timeStamp=" + timeStamp +
                        "&key="+ "bc56e221598fc34298c8af55f2cafff7";//注意这里的参数要根据ASCII码 排序
        paySign = MD5(paySign).toUpperCase(); //将数据MD5加密

        map.put("appId","wx96095d1c2acffb94");
        map.put("timeStamp",timeStamp);
        map.put("nonceStr",nonceStr);
        map.put("packageStr",packageStr);
        map.put("signType",signType);
        map.put("paySign",paySign);
        map.put("prepay_id",prepay_id);
        return map;
    }
    /**
     * 解析XML 获得名称为para的参数值
     * @param xml
     * @param para
     * @return
     */
    public static String getXmlPara(String xml,String para){
        int start = xml.indexOf("<"+para+">");
        int end = xml.indexOf("</"+para+">");

        if(start < 0 && end < 0){
            return null;
        }
        return xml.substring(start + ("<"+para+">").length(),end).replace("<![CDATA[","").replace("]]>","");
    }

    /**
     * 获取ip地址
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            return request.getRemoteAddr();
        }
        byte[] ipAddr = addr.getAddress();
        String ipAddrStr = "";
        for (int i = 0; i < ipAddr.length; i++) {
            if (i > 0) {
                ipAddrStr += ".";
            }
            ipAddrStr += ipAddr[i] & 0xFF;
        }
        return ipAddrStr;
    }

    /**
     * md5
     * @param s
     * @return
     */
    public final static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
