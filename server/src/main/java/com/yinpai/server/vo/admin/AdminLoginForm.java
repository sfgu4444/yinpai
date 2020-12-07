package com.yinpai.server.vo.admin;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class AdminLoginForm {

    @NotEmpty(message = "用户名不能为空")
    @Size(min = 4, max = 16, message = "用户名为6～16个字符")
    private String adminName;

    @NotEmpty(message = "密码不能为空")
    @Size(min = 6, max = 16, message = "密码为6～16个字符")
    private String adminPass;
}
