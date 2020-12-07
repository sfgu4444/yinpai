package com.yinpai.server.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/5 11:59 下午
 */
@Data
@Entity
@Table(name = "yp_user_advice")
public class UserAdvice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private String content;

    private String email;

    private Date createTime;

    private Date updateTime;
}
