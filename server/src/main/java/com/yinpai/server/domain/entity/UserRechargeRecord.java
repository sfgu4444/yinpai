package com.yinpai.server.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "yp_user_recharge_record")
public class UserRechargeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //app充值用户Id
    private Integer userId;

    //商户号
    private Integer adminId;

    //交易类型
    private String type;

    //订单金额
    private Integer money;

    //支付完成时间
    private Date createTime;

    //付款银行
    private String bankType;

    //微信支付订单号
    private String  transactionId;
}
