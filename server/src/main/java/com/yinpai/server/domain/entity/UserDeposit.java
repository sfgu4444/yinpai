package com.yinpai.server.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/21 6:47 下午
 */
@Entity
@Data
@Table(name = "yp_user_deposit")
public class UserDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private Integer payMoney;

    private Integer userMoney;

    private Date createTime;
}
