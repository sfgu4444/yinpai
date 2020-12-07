package com.yinpai.server.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 2:41 下午
 */
@Entity
@Data
@Table(name = "yp_user_follow")
public class UserFollow{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private Integer adminId;

    private Date createTime = new Date();

    private Date updateTime = new Date();

}
