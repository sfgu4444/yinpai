package com.yinpai.server.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/29 8:45 下午
 */
@Data
@Entity
@Table(name = "yp_user_pay")
public class UserPay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private Integer entityId;

    /**
     * 1：单作品
     * 2：商家
     */
    private Integer type;

    private Date expireTime;

    private Date createTime;
}
