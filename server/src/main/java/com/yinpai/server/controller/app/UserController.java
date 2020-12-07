package com.yinpai.server.controller.app;

import com.yinpai.server.service.OriginalApplyService;
import com.yinpai.server.service.SmsService;
import com.yinpai.server.service.UserAdviceService;
import com.yinpai.server.service.UserService;
import com.yinpai.server.vo.PersonalCenterVo;
import com.yinpai.server.vo.UserProfileVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 11:43 上午
 */
@RestController
@RequestMapping("/users")
@Api(tags = "用户相关接口")
public class UserController {

    private final UserService userService;

    private final SmsService smsService;

    private final UserAdviceService userAdviceService;

    private final OriginalApplyService originalApplyService;

    @Autowired
    public UserController(UserService userService, SmsService smsService, UserAdviceService userAdviceService, OriginalApplyService originalApplyService) {
        this.userService = userService;
        this.smsService = smsService;
        this.userAdviceService = userAdviceService;
        this.originalApplyService = originalApplyService;
    }

    @ApiOperation("用户密码登陆")
    @PostMapping("/login/password")
    public String userLoginPassword(@ApiParam("用户名") @RequestParam("username") String username,
                                    @ApiParam("密码") @RequestParam("password") String password) {
        return userService.userLoginPassword(username, password);
    }

    @ApiOperation("发送验证码")
    @PostMapping("/login/sendSms")
    public void sendSms(@ApiParam("手机号") @RequestParam("phone") String phone) {
        smsService.sendSms(phone);
    }

    @ApiOperation("手机号登陆")
    @PostMapping("/login/phone")
    public String userLoginPhone(@ApiParam("手机号") @RequestParam("phone") String phone,
                                 @ApiParam("验证码") @RequestParam("code") Integer code) {
        smsService.checkCode(phone, code);
        return userService.userLoginPhone(phone);
    }

    @ApiOperation("注册")
    @PostMapping("/register")
    public void register(@ApiParam("手机号") @RequestParam("phone") String phone,
                         @ApiParam("验证码") @RequestParam("code") Integer code,
                         @ApiParam("密码") @RequestParam("password") String password) {
        smsService.checkCode(phone, code);
        userService.register(phone, password);
    }

    @ApiOperation("忘记密码")
    @PostMapping("/forget/password")
    public void forgetPassword(@ApiParam("手机号") @RequestParam("phone") String phone,
                               @ApiParam("验证码") @RequestParam("code") Integer code,
                               @ApiParam("密码") @RequestParam("password") String password) {
        smsService.checkCode(phone, code);
        userService.forgetPassword(phone, password);
    }

    @GetMapping("/profile")
    @ApiOperation("用户资料详情")
    public UserProfileVo userProfile() {
        return userService.userProfile();
    }

    @PostMapping("/editProfile")
    @ApiOperation("修改用户资料")
    public void editUserProfile (@RequestBody UserProfileVo vo) {
        userService.editUserProfile(vo);
    }

    @GetMapping("/personalCenter")
    @ApiOperation("/用户个人中心信息")
    public PersonalCenterVo personalCenter() {
        return userService.personalCenter();
    }

    @PostMapping("/advice")
    @ApiOperation("用户建议")
    public void addAdvice(@ApiParam("建议内容") @RequestParam String content,
                          @ApiParam("邮箱") @RequestParam String email) {
        userAdviceService.addAdvice(content, email);
    }

    @PostMapping("/originalApply")
    @ApiOperation("原创入驻")
    public void originalApply(@ApiParam("联系方式") @RequestParam String contactWay,
                              @ApiParam("qq") @RequestParam String qq,
                              @ApiParam("微信") @RequestParam String wx,
                              @ApiParam("邮箱") @RequestParam String email,
                              @ApiParam("作品，多图用英文逗号,分割") @RequestParam String content) {
        originalApplyService.originalApply(contactWay, qq, wx, email, content);
    }

    @PostMapping("/editPhone")
    @ApiOperation("修改手机号")
    public void editPhone(@ApiParam("原手机号") @RequestParam("phone") String phone,
                          @ApiParam("验证码") @RequestParam("code") Integer code,
                          @ApiParam("新手机号") @RequestParam("newPhone") String newPhone) {
        smsService.checkCode(newPhone, code);
        userService.editPhone(phone, newPhone);
    }
}
