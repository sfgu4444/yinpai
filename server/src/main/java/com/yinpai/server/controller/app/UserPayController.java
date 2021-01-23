package com.yinpai.server.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.entity.User;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.log.WebLog;
import com.yinpai.server.service.UserPayService;
import com.yinpai.server.service.UserService;
import com.yinpai.server.service.AdminService;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import com.yinpai.server.vo.AdminPayMethodVo;
import com.yinpai.server.vo.WxPay.JSpayInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/29 11:17 下午
 */
@Slf4j
@RestController
@RequestMapping("/user/pay")
@Api(tags = "用户支付相关")
public class UserPayController {

    private final UserPayService userPayService;

    private final UserService userService;

    private final AdminService adminService;


    @ApiOperation("微信公众号认证")
    @PostMapping("/wechat/auth")
    @WebLog(description = "微信公众号认证")
    @CrossOrigin
    public Map<String, Object> wechatAuth(JSpayInfo jSpayInfo) throws IOException {

        return userPayService.wechatAuth(jSpayInfo.getCode(),jSpayInfo.getTotalFee());
    }

    @Autowired
    public UserPayController(UserPayService userPayService, UserService userService, AdminService adminService) {
        this.userPayService = userPayService;
        this.userService = userService;
        this.adminService = adminService;
    }

    @GetMapping("/work/{workId}")
    @ApiOperation("购买作品")
    @WebLog(description = "购买作品")
    public void userPayWork(@ApiParam("作品ID") @PathVariable Integer workId) {
        userPayService.userPayWork(workId);
    }

    @GetMapping("/admin/{adminId}/{type}/{amount}")
    @ApiOperation("购买商家作品VIP")
    public void userPayAdmin(@ApiParam("商家作者ID") @PathVariable Integer adminId,
                             @ApiParam("类型month、quarter、year") @PathVariable String type,
                             @ApiParam("数量") @PathVariable Integer amount) {
        userPayService.userPayAdmin(adminId, type, amount);
    }

    @GetMapping("/admin/{adminId}/payMethod")
    @ApiOperation("获取商家可支付方式")
    public AdminPayMethodVo adminPayMethod(@ApiParam("商家作者ID") @PathVariable Integer adminId) {
        return userPayService.adminPayMethod(adminId);
    }

    @GetMapping("/money")
    @ApiOperation("查看用户拍币余额")
    public Integer userMoney() {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        User user = userService.findByIdNotNull(userInfoDto.getUserId());
        return user.getMoney();
    }

    @PostMapping("/wechat/{amount}")
    @ApiOperation("微信充值拍币")
    @WebLog(description = "微信充值拍币")
    public WxPayAppOrderResult wechatPayMoney(@ApiParam("充值数量") @PathVariable String amount) {
        return userPayService.wechatPayMoney(amount);
    }

    @PostMapping("/alipay/{amount}")
    @ApiOperation("支付宝充值拍币")
    @WebLog(description = "支付宝充值拍币")
    public String aliPayMoney(@ApiParam("充值数量") @PathVariable String amount) {
        return userPayService.aliPayMoney(amount);
    }

    @PostMapping("/wechat/jsapi/{amount}")
    @ApiOperation("微信公众号充值拍币")
    @WebLog(description = "微信公众号充值拍币")
    public String jsapiPayMoney(@ApiParam("充值数量") @PathVariable String amount) {
        return userPayService.jsapiPayMoney(amount);
    }


}
