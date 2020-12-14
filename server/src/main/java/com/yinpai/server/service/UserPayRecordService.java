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
import org.apache.commons.lang3.ObjectUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

    public String wxAppPayResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获得通知结果
        ServletInputStream inputStream = request.getInputStream();
        String notifyXml = StreamUtils.inputStream2String(inputStream, "utf-8");
        Map<String, String> notifyMap = WXPayUtil.xmlToMap(notifyXml);
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("return_code", "FAIL");
        returnMap.put("return_msg", "");

        if ("SUCCESS".equals(notifyMap.get("result_code")) && "SUCCESS".equals(notifyMap.get("return_code"))) {
            //获取签名
            String orderId = notifyMap.get("out_trade_no");
            Optional<UserOrder> optional = userOrderRepository.findById(Long.parseLong(orderId));

            if (optional.isPresent()) {
                UserOrder userOrder = optional.get();
                String orderMetaData = userOrder.getOrderMetaData();
                JSONObject metaData = new JSONObject(orderMetaData);
                String sign = metaData.getString("sign");
                String total_fee = notifyMap.get("total_fee");

                //校验sign,金额
                if (WXPayUtil.isSignatureValid(notifyXml, sign) && userOrder.getTotalFee().toString().equals(total_fee)) {
                    //接口幂等,未支付
                    if (userOrder.getOrderPayStatus() == 0) {
                        //更新订单状态
                        userOrder.setOrderPayStatus(1);
                        //添加用户余额,获取当前充值人
                        String userId = userOrder.getUserId();
                        User user = userRepository.findUserById(userId);
                        if (ObjectUtils.isNotEmpty(user)) {
                            orderShip(user, userOrder);
                        }
                        //设置响应对象
                        returnMap.put("return_code", "SUCCESS");
                        returnMap.put("return_msg", "OK");
                        String returnXml = WXPayUtil.mapToXml(returnMap);
                        response.setContentType("text/xml");
                        return returnXml;
                    }
                }
            }
        }

        String returnXml = WXPayUtil.mapToXml(returnMap);
        response.setContentType("text/xml");
        log.warn("校验失败");
        return returnXml;
    }

    private void addUserMoney(User user, BigDecimal money) {
        //
        BigDecimal moneyCount = new BigDecimal(user.getMoney()).add(money);
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
    public String AliPayAppPayResult(Map<String, String> map) {
        boolean signVerified = false; //调用SDK验证签名
        try {
            //验签
            signVerified = AlipaySignature.rsaCheckV1(
                    map,
                    alipayPublicKey,
                    charset,
                    signType);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        // 交易状态
        String trade_status = map.get("trade_status");
        String out_trade_no = map.get("out_trade_no");
        // 签名校验
        if (signVerified) {
            // TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，
            //  校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
            if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)) {
                /**
                 * 更新用户支付记录,添加用户充值余额
                 */

                Optional<UserOrder> optional = userOrderRepository.findById(Long.valueOf(out_trade_no));
                if (!optional.isPresent()) return "failure";
                UserOrder userOrder = optional.get();
                String userId = userOrder.getUserId();
                User user = userRepository.findUserById(userId);
                orderShip(user, userOrder);


                return "success";
            }
        } else {
            // TODO 验签失败则记录异常日志，并在response中返回failure.
            log.warn("交易失败,原因:"+map.get("msg"));
            return "failure";
        }
        return "failure";
    }

    @Value("${applepay.boxurl}")
    private String boxurl;

    public PayResultVo appleOrderDetermine(Map<String, String> map) throws JSONException {
        LoginUserInfoDto loginUserInfoDto = LoginUserThreadLocal.get();
        if(null == loginUserInfoDto) throw new NotLoginException("用户必须登录!");

        PayResultVo payResultVo = new PayResultVo();

        JSONObject appleResultJSON = new JSONObject(sendPost(boxurl, JsonUtils.toJsonString(map)));
        String timeStart = appleResultJSON.getJSONObject("receipt").getString("receipt_creation_date_ms");
        String status = appleResultJSON.getString("status");
        if (status.equals("0"))
        //创建订单
        {
            String totalFee = map.get("totalFee");
            System.out.println(totalFee);

            UserOrder userOrder = UserOrder.builder()
                    .orderId(Long.valueOf(map.get("OrderId")))
                    .userId(loginUserInfoDto.getUserId() + "")
                    .body("收集宝")
                    // TODO: 2020/12/14 苹果订单金额单位 未知  订单内单位是分
                    .totalFee(new BigDecimal(1))
                    .ipAddress(ProjectUtil.getIpAddr())
                    .payPlatform("ApplePay")
                    .timeStart(DateUtil.getMMDDYYHHMMSS(new Date(timeStart)))
                    .timeExpire(DateUtil.getMMDDYYHHMMSS(new Date()))
                    .timeStamp(System.currentTimeMillis()+"")
                    .orderPayStatus(0)
                    .orderShipStatus(0)
                    .orderStatus(PayStatus.unpaid)
                    .build();
            userOrderRepository.save(userOrder);

            payResultVo.setCode(200);
            payResultVo.setMsg("充值成功!");

        }else {
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

            while ((line = in.readLine()) != null)
            {
                sb.append(line);
            }
            return JsonUtils.toObject(sb.toString(), Map.class);
        } catch (Exception e) {
            log.warn("发送 POST 请求出现异常！"+e);
            throw new RuntimeException("调用苹果");
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                log.warn("发送 POST 请求出现异常！"+ex);
                throw new RuntimeException("调用苹果");
            }
        }
    }


    private UserOrder orderShip(User user,UserOrder userOrder){
        BigDecimal totalFee = userOrder.getTotalFee();
        Integer orderPayStatus = userOrder.getOrderPayStatus();
        Integer orderShipStatus = userOrder.getOrderShipStatus();
        PayStatus orderStatus = userOrder.getOrderStatus();

        try{
            if (orderStatus == PayStatus.topaid || orderShipStatus == 1){
                userOrder.setOrderStatus(PayStatus.topaid);
            }else if (orderPayStatus == 1){
                // TODO: 2020/12/14  乐观锁
                addUserMoney(user, totalFee.divide(new BigDecimal(100)));
                userOrder.setOrderShipStatus(1);
                userOrder.setOrderStatus(PayStatus.topaid);
                userOrder.setTimeExpire(DateUtil.getMMDDYYHHMMSS(new Date()));
            }
        }catch (Exception e){
            log.warn("订单发货时异常: " + userOrder);
            throw new RuntimeException("订单发货时异常");
        }

        return userOrderRepository.save(userOrder);
    }

}
