package com.yinpai.server.advice;

import com.yinpai.server.domain.dto.LoginAdminInfoDto;
import com.yinpai.server.domain.entity.admin.Admin;
import com.yinpai.server.enums.CommonEnum;
import com.yinpai.server.service.AdminService;
import com.yinpai.server.thread.threadlocal.LoginAdminThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AdminAuthorizeAspect {

    @Autowired
    private AdminService adminService;

    /**
     * 切点
     * @author weilai
     */
    @Pointcut("execution(public * com.yinpai.server.controller.admin.*.*(..))" + "&& !execution(public * com.yinpai.server.controller.admin.LoginController.*(..))")
    public void verify() {

    }

    /**
     * @author weilai
     */
    @Before("verify()")
    public void doVerify(){
        LoginAdminThreadLocal.set(LoginAdminInfoDto.builder().adminId(1).superAdmin(true).isAudit(CommonEnum.NO.getCode()).build());
//        HttpServletRequest request = ProjectUtil.getRequest();
//        Cookie cookie = CookieUtil.get(request, "token");
//        // 没有cookie去登陆
//        if (cookie == null) {
//            if (ProjectUtil.isAjax()) {
//                throw new ProjectException(402, "请先登陆");
//            } else {
//                throw new AdminAuthorizeException();
//            }
//        }
//        Admin admin;
//        try {
//            admin = adminService.getLoginAdmin(cookie.getValue());
//        } catch (Exception e) {
//            if (ProjectUtil.isAjax()) {
//                throw new ProjectException(402, "请先登陆");
//            } else {
//                throw new AdminAuthorizeException();
//            }
//        }
//        if (!admin.getAdminStatus().equals(CommonEnum.YES.getCode())) {
//            if (ProjectUtil.isAjax()) {
//                throw new ProjectException(402, "账号已被停用");
//            } else {
//                throw new AdminAuthorizeException();
//            }
//        }
//        LoginAdminThreadLocal.set(LoginAdminInfoDto.builder().adminId(admin.getId()).isAudit(admin.getIsAudit()).superAdmin(AdminService.superAdminIds.contains(admin.getId())).build());
    }
}
