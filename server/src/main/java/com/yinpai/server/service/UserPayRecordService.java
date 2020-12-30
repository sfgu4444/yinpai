package com.yinpai.server.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.github.wxpay.sdk.WXPayUtil;
import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.BaseFilterDto;
import com.yinpai.server.domain.entity.User;
import com.yinpai.server.domain.entity.UserOrder;
import com.yinpai.server.domain.entity.UserPayRecord;
import com.yinpai.server.domain.entity.UserRechargeRecord;
import com.yinpai.server.domain.entity.admin.Admin;
import com.yinpai.server.domain.repository.UserOrderRepository;
import com.yinpai.server.domain.repository.UserPayRecordRepository;
import com.yinpai.server.domain.repository.UserRechargeRecordRepository;
import com.yinpai.server.domain.repository.UserRepository;
import com.yinpai.server.enums.PayStatus;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import com.yinpai.server.utils.*;
import com.yinpai.server.vo.PayRecordListVo;
import com.yinpai.server.vo.WxPay.PayResultVo;
import com.yinpai.server.vo.WxPay.WxPayResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.net.ssl.*;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Calendar.MINUTE;
import static java.util.Calendar.getInstance;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/21 6:27 下午
 */
@Service
@Slf4j
@Transactional
public class UserPayRecordService {

    private final UserPayRecordRepository userPayRecordRepository;

    private final AdminService adminService;

    private final WorksService worksService;

    @Autowired
    public UserPayRecordService(UserPayRecordRepository userPayRecordRepository, @Lazy AdminService adminService, @Lazy WorksService worksService) {
        this.userPayRecordRepository = userPayRecordRepository;
        this.adminService = adminService;
        this.worksService = worksService;
    }


    public PageResponse<PayRecordListVo> list(Integer userId, BaseFilterDto baseFilterDto) {
        Page<UserPayRecord> payRecordPage = userPayRecordRepository.findAllByUserId(userId, baseFilterDto.getSetPageable());
        List<PayRecordListVo> voList = new ArrayList<>();
        payRecordPage.forEach(r -> {
            PayRecordListVo vo = new PayRecordListVo();
            Admin admin = adminService.findById(r.getAdminId());
            if (admin != null) {
                vo.setNickName(admin.getNickName());
            }
            vo.setMoney(r.getMoney());
            vo.setCreateTime(r.getCreateTime());
            voList.add(vo);
        });
        return PageResponse.of(voList, baseFilterDto.getPageable(), payRecordPage.getTotalElements());
    }

    @Autowired
    private UserRechargeRecordRepository userRechargeRecordRepository;

    @Autowired
    private UserOrderRepository userOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${wechat.opMchkey}")
    private String opMchkey;


    /**
     * 1:去空
     * 2:拼接
     * 3:加入秘钥key
     */
    public String wxAppPayResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //接收xml
        ServletInputStream is = request.getInputStream();
        //将InputStream转换成String
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        String resXml = "";
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                log.error("数据转换异常", e);
            }
        }
        resXml = sb.toString();
        log.info("【微信回调】: {}", resXml);
        Map<String, String> notifyMap = WXPayUtil.xmlToMap(resXml);
        String sign = notifyMap.get("sign");
        notifyMap = PayUtil.paraFilter(notifyMap);
        Map<String, String> returnMap = new HashMap<>();
        if ("SUCCESS".equals(notifyMap.get("result_code")) && "SUCCESS".equals(notifyMap.get("return_code"))) {
            String orderId = notifyMap.get("out_trade_no");
            Optional<UserOrder> optional = userOrderRepository.findById(Long.parseLong(orderId));
            if (optional.isPresent()) {
                UserOrder userOrder = optional.get();
                log.info("索取订单数据:{}", userOrder);
                String total_fee = notifyMap.get("total_fee");
                int retval = userOrder.getTotalFee().compareTo(new BigDecimal(total_fee));
                log.info("【retval】: {} : 【userOrder】 , {} , 【total_fee】 : {} ,【orderPayStatus】 : {} ,【sign】: {} ,【订单ID】: {}", retval, userOrder.getTotalFee(), total_fee, userOrder.getOrderPayStatus(), sign, orderId);
                //转换
                String stringA = PayUtil.createLinkString(notifyMap);
                //拼接API密钥
                String signResult = PayUtil.sign(stringA, opMchkey, "UTF-8").toUpperCase();
                //对比
                //boolean flag = PayUtil.verify(signResult, sign, opMchkey, "UTF-8");
                if (sign.equals(signResult)) {
                    //if (flag) {
                    //todo 测试对比 retval为 0, 线上更新为订单原始价格 retval == total_fee
                    if (retval == 0) {
                        log.info("订单状态:{}", userOrder.getOrderPayStatus());
                        if (userOrder.getOrderPayStatus() == 0) {
                            //更新订单状态
                            userOrder.setOrderPayStatus(1);
                            //添加用户余额,获取当前充值人
                            String userId = userOrder.getUserId();
                            User user = userRepository.findById(Integer.valueOf(userId)).orElseGet(User::new);
                            if (ObjectUtils.isNotEmpty(user)) {
                                //添加余额
                                orderShip(user, userOrder);
                            }
                            //设置响应对象
                            returnMap.put("return_code", "SUCCESS");
                            returnMap.put("return_msg", "OK");
                            String returnXml = WXPayUtil.mapToXml(returnMap);
                            response.setContentType("text/xml");
                            log.warn(user.getUsername(), "用户充值成功");
                            return returnXml;
                        }
                    }
                }
            }
        }
        returnMap.put("return_code", "FAIL");
        returnMap.put("return_msg", "");
        String returnXml = WXPayUtil.mapToXml(returnMap);
        response.setContentType("text/xml");
        log.warn("校验失败");
        return returnXml;
    }

    private void addUserMoney(User user, BigDecimal money) {
        // todo 为了保存整数
        BigDecimal moneyCount = new BigDecimal(user.getMoney()).add(new BigDecimal(100));
        // BigDecimal moneyCount = new BigDecimal(user.getMoney()).add(money);
        user.setMoney(moneyCount.intValue());
        userRepository.save(user);
    }

    private UserRechargeRecord wxPayResultChangeUserRechargeRecord(WxPayResult wxPayResult, Integer userId) {
        UserRechargeRecord userRechargeRecord = new UserRechargeRecord();
        userRechargeRecord.setUserId(userId);
        userRechargeRecord.setAdminId(Integer.parseInt(wxPayResult.getMch_id()));
        userRechargeRecord.setBankType(wxPayResult.getBank_type());
        userRechargeRecord.setCreateTime(DateUtil.parseMMDDSS(wxPayResult.getTime_end()));
        userRechargeRecord.setMoney(Integer.parseInt(wxPayResult.getTotal_fee()));
        userRechargeRecord.setTransactionId(wxPayResult.getTransaction_id());
        userRechargeRecord.setType(wxPayResult.getTrade_type());
        return userRechargeRecord;
    }

    public UserRechargeRecord save(UserRechargeRecord userRechargeRecord) {
        return userRechargeRecordRepository.save(userRechargeRecord);
    }


    public UserPayRecord save(UserPayRecord userPayRecord) {
        return userPayRecordRepository.save(userPayRecord);
    }

    @Value("${alipay.alipayPublicKey}")
    private String alipayPublicKey;

    @Value("${alipay.charset}")
    private String charset;

    @Value("${alipay.signType}")
    private String signType;

    //支付宝回调接口
    public String AliPayAppPayResult(HttpServletRequest request) {
        //调用SDK验证签名
        boolean signVerified = false;
        Map requestParams = request.getParameterMap();
        Map<String, String> params = new HashMap<String, String>();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            System.out.println(">>>>>参数" + name + ":" + valueStr);
            params.put(name, valueStr);
        }
        log.info("接收回调数据成功", params);
        try {
            //验签
            signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    alipayPublicKey,
                    charset,
                    signType);
        } catch (AlipayApiException e) {
            log.error("验签异常", e);
        }
        // 交易状态
        String trade_status = params.get("trade_status");
        String out_trade_no = params.get("out_trade_no");
        // 签名校验
        if (signVerified) {
            if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)) {
                Optional<UserOrder> optional = userOrderRepository.findById(Long.valueOf(out_trade_no));
                if (!optional.isPresent()) return "failure";
                UserOrder userOrder = optional.get();
                String userId = userOrder.getUserId();
                User user = userRepository.findUserById(userId);
                orderShip(user, userOrder);
                return "success";
            }
        } else {
            log.warn("交易失败,原因:" + params.get("msg"));
            return "failure";
        }
        return "failure";
    }

    @Value("${applepay.boxurl}")
    private String boxurl;

    public PayResultVo appleOrderDetermine(Map<String, Object> map) throws JSONException {
        LoginUserInfoDto loginUserInfoDto = LoginUserThreadLocal.get();
        if (null == loginUserInfoDto) throw new NotLoginException("用户必须登录!");

        PayResultVo payResultVo = new PayResultVo();
        String str = JsonUtils.toJsonString(map);
        System.out.println(str);
        //String verifyResult = IosVerifyUtil.buyAppVerify(payload, 1);
        //String bs = Base64Utils.encode(str.getBytes());
        JSONObject appleResultJSON = new JSONObject(IosVerifyUtil.buyAppVerify(boxurl, str));
        log.info("【苹果验证结果】: 【{}】", appleResultJSON);
        //JSONObject appleResultJSON = new JSONObject(sendPost(boxurl, str));
        // todo 没有这个参数
        String status = appleResultJSON.getString("status");
        if (status.equals("0"))
        //创建订单
        {
//            String totalFee = map.get("totalFee").toString();
//            System.out.println(totalFee);
//            //String timeStart = appleResultJSON.getJSONObject("receipt").getString("receipt_creation_date_ms");
//            UserOrder userOrder = UserOrder.builder()
//                    .orderId(Long.valueOf(map.get("OrderId").toString()))
//                    .userId(loginUserInfoDto.getUserId() + "")
//                    .body("收集宝")
//                    // TODO: 2020/12/14 苹果订单金额单位 未知  订单内单位是分
//                    .totalFee(new BigDecimal(1))
//                    .ipAddress(ProjectUtil.getIpAddr())
//                    .payPlatform("ApplePay")
//                    .timeStart(DateUtil.getMMDDYYHHMMSS(new Date()))
//                    .timeExpire(DateUtil.getMMDDYYHHMMSS(new Date()))
//                    .timeStamp(System.currentTimeMillis()+"")
//                    .orderPayStatus(0)
//                    .orderShipStatus(0)
//                    .orderStatus(PayStatus.unpaid)
//                    .build();
//            userOrderRepository.save(userOrder);

            payResultVo.setCode(200);
            payResultVo.setMsg("充值成功!");

        } else {
            payResultVo.setCode(400);
            payResultVo.setMsg("充值失败!");
        }
        return payResultVo;

    }

    private Map sendPost(String url, String param) {
        StringBuilder sb = null;
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print("{\"receipt-data\": \"" + param + "\"}");
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            sb = new StringBuilder();

            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            return JsonUtils.toObject(sb.toString(), Map.class);
        } catch (Exception e) {
            log.warn("发送 POST 请求出现异常！" + e);
            throw new RuntimeException("调用苹果");
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                log.warn("发送 POST 请求出现异常！" + ex);
                throw new RuntimeException("调用苹果");
            }
        }
    }


    private UserOrder orderShip(User user, UserOrder userOrder) {
        BigDecimal totalFee = userOrder.getTotalFee();
        Integer orderPayStatus = userOrder.getOrderPayStatus();
        Integer orderShipStatus = userOrder.getOrderShipStatus();
        PayStatus orderStatus = userOrder.getOrderStatus();
        try {
            if (orderStatus == PayStatus.topaid || orderShipStatus == 1) {
                userOrder.setOrderStatus(PayStatus.topaid);
            } else if (orderPayStatus == 1) {
                // TODO: 2020/12/14  乐观锁
                addUserMoney(user, totalFee.divide(new BigDecimal(100)));
                userOrder.setOrderShipStatus(1);
                userOrder.setOrderStatus(PayStatus.topaid);
                userOrder.setTimeExpire(DateUtil.getMMDDYYHHMMSS(new Date()));
            }
        } catch (Exception e) {
            log.warn("订单发货时异常: " + userOrder);
            throw new RuntimeException("订单发货时异常");
        }

        return userOrderRepository.save(userOrder);
    }

}