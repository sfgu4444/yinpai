package com.yinpai.server.domain.entity;

import com.yinpai.server.enums.PayStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "yp_user_order")
public class UserOrder {

    //商户订单号(唯一订单ID)
    @Id
    //@GeneratedValue
    private Long orderId;

    //用户ID(User中的ID)
    private String userId;

    //商品的名字
    private String body;

    //商品的价格
    private BigDecimal totalFee;

    //客户端IP地址
    private String ipAddress;

    //付款方式(支付宝,微信,苹果)
    private String payPlatform;

    //交易开始时间
    private String timeStart;

    //交易结束时间
    private String timeExpire;

    //时间戳(订单存入数据库)
    private String timeStamp;

    //订单付款状态(0未付款,1已付款)
    private Integer orderPayStatus;

    //订单出货状态(0未出货,1已出货)
    private Integer orderShipStatus;

    //订单状态(0进行中,1订单结束,2订单异常)
    private PayStatus orderStatus;

    //订单附带数据(JSON格式储存)
    private String orderMetaData;

    //版本号
    private String version;

}
