package com.yinpai.server.controller.admin;

import com.yinpai.server.exception.ProjectException;
import com.yinpai.server.service.AdminService;
import com.yinpai.server.utils.ResultUtil;
import com.yinpai.server.vo.admin.AdminLoginForm;
import com.yinpai.server.vo.admin.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Objects;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 4:02 下午
 */
@Controller
@RequestMapping("/admin")
public class LoginController {

    private final AdminService adminService;

    @Autowired
    public LoginController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView();
    }

    @PostMapping("/login")
    @ResponseBody
    public ResultVO loginData(@Valid AdminLoginForm adminLoginForm,
                              BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new ProjectException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        adminService.login(adminLoginForm.getAdminName(), adminLoginForm.getAdminPass());
        return ResultUtil.success("登陆成功");
    }

    @GetMapping("/logout")
    public ModelAndView logout(){
        adminService.logout();
        return new ModelAndView("admin/login");
    }

}
