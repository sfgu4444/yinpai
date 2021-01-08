package com.yinpai.server.domain.entity.admin;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @program server
 * @description: 提现
 * @author: liuzhenda
 * @create: 2021/01/06 14:07
 */
@Entity
@Data
@Table(name = "yp_withdrawal")
public class Withdrawal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int adminId;

    private String adminName;

    private int money;

    private int state;

    private Long bankAccount;

    private Date createTime = new Date();

}
