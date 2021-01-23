package com.yinpai.server.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.github.wxpay.sdk.WXPayUtil;
import com.google.gson.Gson;
import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.BaseFilterDto;
import com.yinpai.server.domain.entity.*;
import com.yinpai.server.domain.entity.admin.Admin;
import com.yinpai.server.domain.repository.*;
import com.yinpai.server.enums.PayStatus;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.exception.ProjectException;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import com.yinpai.server.utils.*;
import com.yinpai.server.vo.PayDepositListVo;
import com.yinpai.server.vo.PayRecordListVo;
import com.yinpai.server.vo.WxPay.PayResultVo;
import com.yinpai.server.vo.WxPay.WxPayResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
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

    private final UserDepositRepository userDepositRepository;

    @Autowired
    public UserPayRecordService(UserPayRecordRepository userPayRecordRepository, @Lazy AdminService adminService, @Lazy WorksService worksService, UserDepositRepository userDepositRepository) {
        this.userPayRecordRepository = userPayRecordRepository;
        this.adminService = adminService;
        this.worksService = worksService;
        this.userDepositRepository = userDepositRepository;
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
                            this.saveOrderLog(userOrder);
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
        log.info("【添加拍币】 原 : {} ，现 : {}",user.getMoney(),moneyCount.intValue());
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

    // 将request中的参数转换成Map
    private Map<String, String> convertParamsToMap(Map requestParams) {
        Map<String, String> retMap = new HashMap();
        Set<Map.Entry<String, List>> entrySet = requestParams.entrySet();
        for (Map.Entry<String, List> entry : entrySet) {
            String name = entry.getKey();
            List values = entry.getValue();
            int valLen = values.size();
            if (valLen == 1) {
                retMap.put(name, (String) values.get(0));
            } else if (valLen > 1) {
                StringBuilder sb = new StringBuilder();
                for (Object val : values) {
                    sb.append(",").append(val);
                }
                retMap.put(name, sb.toString().substring(1));
            } else {
                retMap.put(name, "");
            }
        }
        return retMap;
    }

    //支付宝回调接口
    public String AliPayAppPayResult(HttpServletRequest request) throws IOException {
        Map<String, String> map = convertRequestParamsToMap(request);
        log.info("【支付宝结果回调】 {}", new Gson().toJson(map));
        //调用SDK验证签名
        boolean signVerified = false;
        try {
            //验签
            signVerified = AlipaySignature.rsaCheckV1(
                    map,
                    alipayPublicKey,
                    charset,
                    signType);
        } catch (AlipayApiException e) {
            log.error("验签异常", e);
        }
        // 交易状态
        String trade_status = map.get("trade_status");
        String out_trade_no = map.get("out_trade_no");
        // 签名校验
        log.info("【签名校验】 {}", signVerified);
        if (signVerified) {
            if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)) {
                Optional<UserOrder> optional = userOrderRepository.findById(Long.valueOf(out_trade_no));
                if (!optional.isPresent()) {
                    log.error("未查询到订单信息");
                    return "failure";
                }
                UserOrder userOrder = optional.get();
                Integer userId = Integer.parseInt(userOrder.getUserId());
                User user = userRepository.findUserById(userId);
                orderShip(user, userOrder);
                this.saveOrderLog(userOrder);
                return "success";
            }
        } else {
            log.warn("交易失败,原因:" + map.get("msg"));
            return "failure";
        }
        log.warn("交易失败,原因:" + map.get("msg"));
        return "failure";
    }


    public PayResultVo appleOrderDetermine(Map<String, Object> map) throws JSONException {
        LoginUserInfoDto loginUserInfoDto = LoginUserThreadLocal.get();
        if (null == loginUserInfoDto) {
            throw new NotLoginException("用户必须登录!");
        }
        log.info("【苹果支付】 userID : {}",loginUserInfoDto.getUserId());
        PayResultVo payResultVo = new PayResultVo();
        Integer statusFont = (Integer) map.get("status");
        Calendar expire = getInstance();
        expire.add(MINUTE, 10);
        LinkedHashMap mapResult = (LinkedHashMap) map.get("receipt");
        List<LinkedHashMap> inAppResult = (List<LinkedHashMap>) mapResult.get("in_app");
        Long orderId = Long.valueOf(inAppResult.get(0).get("transaction_id").toString());
        String price = inAppResult.get(0).get("product_id").toString();
        String totalFee="";
        if(price != null && !"".equals(price)){
            for(int i=0;i<price.length();i++){
                if(price.charAt(i)>=48 && price.charAt(i)<=57){
                    totalFee+=price.charAt(i);
                }
            }
        }

        if (0 != statusFont) {
            //存储错误
            UserOrder userOrder = UserOrder.builder()
                    .orderId(orderId)
                    .userId(loginUserInfoDto.getUserId() + "")
                    .body("收集宝")
                    .totalFee(new BigDecimal(totalFee))
                    .ipAddress(ProjectUtil.getIpAddr())
                    .payPlatform("APPLE")
                    //.orderMetaData(receipt)
                    .orderMetaData(new Gson().toJson(mapResult))
                    .timeStart(DateUtil.getMMDDYYHHMMSS(new Date()))
                    .timeExpire(DateUtil.getMMDDYYHHMMSS(expire.getTime()))
                    .timeStamp(System.currentTimeMillis() + "")
                    .orderPayStatus(0)
                    .orderShipStatus(0)
                    .orderStatus(PayStatus.expaid)
                    .build();
            userOrderRepository.save(userOrder);
            throw new RuntimeException("支付失败");
        }

         /*String environment = (String) map.get("environment");
        JSONObject appleResultJSON = null;
       if (environment.equals("Sandbox")) {
            appleResultJSON = new JSONObject(IosVerifyUtil.buyAppVerify(receipt, 0));
        } else {
            appleResultJSON = new JSONObject(IosVerifyUtil.buyAppVerify(receipt, 1));
        }
        log.info("【苹果验证结果】: 【{}】", appleResultJSON);
        String status = appleResultJSON.getString("status");*/
        if (0 == statusFont) {
            UserOrder userOrder = UserOrder.builder()
                    .orderId(orderId)
                    .userId(loginUserInfoDto.getUserId() + "")
                    .body("收集宝")
                    .totalFee(new BigDecimal(totalFee))
                    .ipAddress(ProjectUtil.getIpAddr())
                    .payPlatform("APPLE")
                    .orderMetaData(new Gson().toJson(mapResult))
                    .timeStart(DateUtil.getMMDDYYHHMMSS(new Date()))
                    .timeExpire(DateUtil.getMMDDYYHHMMSS(expire.getTime()))
                    .timeStamp(System.currentTimeMillis() + "")
                    .orderPayStatus(0)
                    .orderShipStatus(0)
                    .orderStatus(PayStatus.unpaid)
                    .build();
            UserOrder order = userOrderRepository.save(userOrder);
            this.saveOrderLog(order);
            User user = userRepository.findUserById(loginUserInfoDto.getUserId());
            orderShip(user, order);
            payResultVo.setCode(HttpStatus.OK);
            payResultVo.setMsg("充值成功!");
        } else {
            payResultVo.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
            payResultVo.setMsg("充值失败");
        }
        return payResultVo;
    }

    private UserOrder orderShip(User user, UserOrder userOrder) {
        BigDecimal totalFee = userOrder.getTotalFee();
        Integer orderPayStatus = userOrder.getOrderPayStatus(); //订单付款状态(0未付款,1已付款)
        Integer orderShipStatus = userOrder.getOrderShipStatus();//订单出货状态(0未出货,1已出货)
        PayStatus orderStatus = userOrder.getOrderStatus(); //订单状态 unpaid,expaid, topaid;
        try {
            if (orderStatus == PayStatus.unpaid || orderShipStatus == 0 || orderPayStatus == 0) {
                //加钱
                addUserMoney(user, totalFee.divide(new BigDecimal(100)));
                userOrder.setOrderShipStatus(1);
                userOrder.setOrderStatus(PayStatus.topaid);
                userOrder.setOrderPayStatus(1);
                userOrder.setTimeExpire(DateUtil.getMMDDYYHHMMSS(new Date()));
            }
        } catch (Exception e) {
            log.warn("订单异常: " + userOrder);
            userOrder.setOrderStatus(PayStatus.expaid);
            throw new RuntimeException("订单发货时异常");
        }

        return userOrderRepository.save(userOrder);
    }

    public Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, String> retMap = new HashMap<String, String>();
        Set<Map.Entry<String, String[]>> entrySet = request.getParameterMap().entrySet();
        for (Map.Entry<String, String[]> entry : entrySet) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            int valLen = values.length;
            if (valLen == 1) {
                retMap.put(name, values[0]);
            } else if (valLen > 1) {
                StringBuilder sb = new StringBuilder();
                for (String val : values) {
                    sb.append(",").append(val);
                }
                retMap.put(name, sb.toString().substring(1));
            } else {
                retMap.put(name, "");
            }
        }

        return retMap;
    }

    /**
     * todo 没写查询
     * @param map
     * @param pageable
     * @return
     */
    public Page<UserPayRecord> findFilterAll(Map<String, String> map, Pageable pageable) {
        Page<UserPayRecord> recordRepositoryAll =  userPayRecordRepository.findAll(ProjectUtil.getSpecification(map), pageable);
        for (UserPayRecord userPayRecord : recordRepositoryAll){
            User user  = userRepository.findUserById(userPayRecord.getUserId());
            if(null != user){
                userPayRecord.setUserName(user.getUsername());
            }
            Admin admin = adminService.findById(userPayRecord.getAdminId());
            if(null != admin){
                userPayRecord.setAdminName(admin.getAdminName());
            }
        }
        return userPayRecordRepository.findAll(ProjectUtil.getSpecification(map), pageable);
    }

    public void delete(Integer id) {
        UserPayRecord userPayRecord = findByIdNotNull(id);
        userPayRecordRepository.delete(userPayRecord);
    }

    public UserPayRecord findByIdNotNull(Integer id) {
        return userPayRecordRepository.findById(id).orElseThrow(() -> new ProjectException("支付记录不存在"));
    }

    /**
     * 插入支付记录表
     * @param userOrder
     */
    public void saveOrderLog(UserOrder userOrder){
        log.info("【插入支付记录表】: {}",userOrder.getOrderId());
        try {
            UserDeposit dto = new UserDeposit();
            dto.setPayMoney(userOrder.getTotalFee().intValue());
            dto.setUserMoney(userOrder.getTotalFee().intValue());
            dto.setCreateTime(new Date());
            dto.setUserId(Integer.valueOf(userOrder.getUserId()));
            userDepositRepository.save(dto);
        }catch (Exception e){
            log.error("【插入支付记录表异常】: {}  {}",e.getMessage(),e);
        }
    }

    public String jsApiPayResult(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        log.info("【公众号微信回调】: {}", resXml);
        return "";
    }
}