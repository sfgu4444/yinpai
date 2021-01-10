package com.yinpai.server.vo.admin;

import com.yinpai.server.domain.entity.User;
import lombok.Data;

import javax.persistence.Entity;
import java.util.Date;

/**
 * @program server
 * @description: 意见反馈
 * @author: liuzhenda
 * @create: 2021/01/10 12:55
 */

@Data
public class Advice {

    private Integer id;

    private Integer userId;

    private String content;

    private String email;

    private Date createTime;

    private Date updateTime;

    private User user;
}
