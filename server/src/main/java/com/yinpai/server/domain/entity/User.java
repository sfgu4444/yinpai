package com.yinpai.server.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 11:23 上午
 */
@Entity
@Table(name = "yp_user")
@Data
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String nickName;

    private String password;

    private String phone;

    private String avatarUrl;

    private String backgroundUrl;

    private Integer gender;

    private String area;

    private Integer status;

    private Integer money;

    private String personalSignature;

    private Date createTime = new Date();

    private Date updateTime = new Date();
}
