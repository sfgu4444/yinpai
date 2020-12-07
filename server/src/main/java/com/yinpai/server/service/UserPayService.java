package com.yinpai.server.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.yinpai.server.config.AlipayConfig;
import com.yinpai.server.config.WechatConfig;
import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.entity.User;
import com.yinpai.server.domain.entity.UserPay;
import com.yinpai.server.domain.entity.UserPayRecord;
import com.yinpai.server.domain.entity.Works;
import com.yinpai.server.domain.entity.admin.Admin;
import com.yinpai.server.domain.repository.UserPayRepository;
import com.yinpai.server.exception.NotAcceptableException;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.exception.ProjectException;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import com.yinpai.server.utils.DateUtil;
import com.yinpai.server.utils.ProjectUtil;
import com.yinpai.server.vo.AdminPayMethodVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.MINUTE;
import static java.util.Calendar.getInstance;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/29 8:54 下午
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class UserPayService {

    private final UserPayRepository userPayRepository;

    private final UserService userService;

    private final WorksService worksService;

    private final AdminService adminService;

    private final WechatConfig wechatConfig;

    private final AlipayConfig alipayConfig;

    private final UserPayRecordService userPayRecordService;

    @Autowired
    public UserPayService(UserPayRepository userPayRepository, UserService userService, WorksService worksService, AdminService adminService, WechatConfig wechatConfig, AlipayConfig alipayConfig, @Lazy UserPayRecordService userPayRecordService) {
        this.userPayRepository = userPayRepository;
        this.userService = userService;
        this.worksService = worksService;
        this.adminService = adminService;
        this.wechatConfig = wechatConfig;
        this.alipayConfig = alipayConfig;
        this.userPayRecordService = userPayRecordService;
    }

    public boolean isPayWork(Integer workId, Integer adminId, Integer userId) {
        UserPay workPay = userPayRepository.findByEntityIdAndUserIdAndType(workId, userId, 1);
        if (workPay != null) {
            return true;
        }
        return isPayAdmin(adminId, userId);
    }

    public boolean isPayAdmin(Integer adminId, Integer userId) {
        UserPay adminPay = userPayRepository.findByEntityIdAndUserIdAndType(adminId, userId, 2);
        if (adminPay == null) {
            return false;
        }
        return new Date().compareTo(adminPay.getExpireTime()) < 0;
    }

    public void userPayWork(Integer workId) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        Works works = worksService.findByIdNotNull(workId);
        if (isPayWork(workId, works.getAdminId(), userInfoDto.getUserId())) {
            throw new NotAcceptableException("已经购买过了");
        }
        synchronized (this) {
            User user = userService.findByIdNotNull(userInfoDto.getUserId());
            int balance = user.getMoney() - works.getPrice();
            if (balance < 0) {
                throw new NotAcceptableException("余额不足");
            }
            user.setMoney(balance);
            userService.save(user);
        }
        UserPay userPay = new UserPay();
        userPay.setUserId(userInfoDto.getUserId());
        userPay.setEntityId(workId);
        userPay.setType(1);
        userPay.setCreateTime(new Date());
        userPayRepository.save(userPay);
        UserPayRecord userPayRecord = new UserPayRecord();
        userPayRecord.setUserId(userInfoDto.getUserId());
        userPayRecord.setAdminId(works.getAdminId());
        userPayRecord.setType(works.getType());
        userPayRecord.setMoney(works.getPrice());
        userPayRecord.setCreateTime(new Date());
        userPayRecordService.save(userPayRecord);
    }

    public void userPayAdmin(Integer adminId, String type, Integer amount) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        Admin admin = adminService.findByIdNotNull(adminId);
        int price;
        Date expireTime;
        UserPay userPay = userPayRepository.findByEntityIdAndUserIdAndType(adminId, userInfoDto.getUserId(), 2);
        if (userPay == null) {
            expireTime = new Date();
        } else {
            expireTime = userPay.getExpireTime();
        }
        if ("month".equalsIgnoreCase(type)) {
            if (admin.getMonthPayPrice() <= 0) {
                throw new NotAcceptableException("该作者暂未开通月付方式");
            }
            price = admin.getMonthPayPrice() * amount;
            expireTime = DateUtil.timeAdd(expireTime, Calendar.MONTH, amount);
        } else if ("quarter".equalsIgnoreCase(type)) {
            if (admin.getQuarterPayPrice() <= 0) {
                throw new NotAcceptableException("该作者暂未开通季付方式");
            }
            price = admin.getQuarterPayPrice() * amount;
            expireTime = DateUtil.timeAdd(expireTime, Calendar.MONTH, amount * 3);
        } else if ("year".equalsIgnoreCase(type)) {
            if (admin.getYearPayPrice() <= 0) {
                throw new NotAcceptableException("该作者暂未开通年付方式");
            }
            price = admin.getYearPayPrice() * amount;
            expireTime = DateUtil.timeAdd(expireTime, Calendar.YEAR, amount);
        } else {
            throw new NotAcceptableException("类型错误");
        }
        synchronized (this) {
            User user = userService.findByIdNotNull(userInfoDto.getUserId());
            int balance = user.getMoney() - price;
            if (balance < 0) {
                throw new NotAcceptableException("余额不足");
            }
            user.setMoney(balance);
            userService.save(user);
        }
        if (userPay == null){
            userPay = new UserPay();
            userPay.setUserId(userInfoDto.getUserId());
            userPay.setEntityId(adminId);
            userPay.setType(2);
            userPay.setCreateTime(new Date());
        }
        userPay.setExpireTime(expireTime);
        userPayRepository.save(userPay);
        UserPayRecord userPayRecord = new UserPayRecord();
        userPayRecord.setUserId(userInfoDto.getUserId());
        userPayRecord.setAdminId(adminId);
        userPayRecord.setType(3);
        userPayRecord.setMoney(price);
        userPayRecord.setCreateTime(new Date());
        userPayRecordService.save(userPayRecord);
    }

    public AdminPayMethodVo adminPayMethod(Integer adminId) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        Admin admin = adminService.findByIdNotNull(adminId);
        AdminPayMethodVo vo = new AdminPayMethodVo();
        vo.setMonthPayPrice(admin.getMonthPayPrice());
        vo.setQuarterPayPrice(admin.getQuarterPayPrice());
        vo.setYearPayPrice(admin.getYearPayPrice());
        UserPay userPay = userPayRepository.findByEntityIdAndUserIdAndType(adminId, userInfoDto.getUserId(), 2);
        if (userPay != null) {
            vo.setExpireTime(userPay.getExpireTime());
        }
        return vo;
    }

    public WxPayAppOrderResult wechatPayMoney(String amount) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        WxPayService wxPayService = new WxPayServiceImpl();
        WxPayConfig wxPayConfig = wechatConfig.appPayConfig();
        wxPayConfig.setTradeType("APP");
        wxPayService.setConfig(wxPayConfig);
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody("收集宝");
        // TODO
//        orderRequest.setOutTradeNo(orderEntity.getOrderNo());
//        orderRequest.setTotalFee(BaseWxPayRequest.yuanToFen(orderEntity.getOrderPrice().toString()));
        orderRequest.setSpbillCreateIp(ProjectUtil.getIpAddr());
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar expire = getInstance();
        expire.add(MINUTE, 10);
        orderRequest.setTimeStart(timeFormat.format(new Date()));
        orderRequest.setTimeExpire(timeFormat.format(expire.getTime()));
        try {
            return wxPayService.createOrder(orderRequest);
        } catch (WxPayException e) {
            // TODO
//            log.error("【唤醒微信APP支付失败】订单ID：{}, 信息：{}", orderEntity.getId(), e.getMessage());
            throw new ProjectException("微信统一下单失败");
        }
    }

    public String aliPayMoney(String amount) {
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.getServerUrl(), alipayConfig.getAppId(), alipayConfig.getAppPrivateKey(), "json", alipayConfig.getCharset(), alipayConfig.getAlipayPublicKey(), alipayConfig.getSignType());

        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody("收集宝");
        model.setSubject("购买商品");
        // TODO
//        model.setOutTradeNo(orderEntity.getOrderNo());
        model.setTimeoutExpress(alipayConfig.getTimeoutExpress());
        // TODO
//        model.setTotalAmount(orderEntity.getOrderPrice().stripTrailingZeros().toPlainString());
        /*
         * 1.电脑网站支付产品alipay.trade.page.pay接口中，product_code为：FAST_INSTANT_TRADE_PAY
         * 2.手机网站支付产品alipay.trade.wap.pay接口中，product_code为：QUICK_WAP_WAY
         * 3.当面付条码支付产品alipay.trade.pay接口中，product_code为：FACE_TO_FACE_PAYMENT
         * 4.APP支付产品alipay.trade.app.pay接口中，product_code为：QUICK_MSECURITY_PAY
         */
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(alipayConfig.getNotifyUrl());

        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            //就是orderString 可以直接给客户端请求，无需再做处理。
            return response.getBody();
        } catch (AlipayApiException e) {
            // TODO
//            log.error("【唤醒支付宝APP支付失败】订单ID：{}, 信息：{}", orderEntity.getId(), e.getMessage());
            throw new ProjectException("支付宝统一下单失败");
        }
    }
}
