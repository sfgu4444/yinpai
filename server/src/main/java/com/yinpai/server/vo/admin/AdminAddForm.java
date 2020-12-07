package com.yinpai.server.vo.admin;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class AdminAddForm {

    private Integer id;

    @NotEmpty(message = "用户名不能为空")
    @Size(min = 4, max = 16, message = "用户名为4～16个字符")
    private String adminName;

    private String adminPass;

    private String adminCheckPass;

    private String backgroundUrl;

    private String nickName;

    private String avatarUrl;

    private Integer adminStatus;

    private Integer monthPayPrice;

    private Integer quarterPayPrice;

    private Integer yearPayPrice;
}
