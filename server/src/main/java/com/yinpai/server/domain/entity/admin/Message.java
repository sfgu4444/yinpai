package com.yinpai.server.domain.entity.admin;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @program server
 * @description: 消息
 * @author: liuzhenda
 * @create: 2021/01/04 22:16
 */
@Entity
@Data
@Table(name = "yp_message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //发送者ID
    private Integer send;
    private String sendName;
    //接收者ID
    private Integer receive;
    private String receiveName;
    //消息
    private String message;
    //已读1 未读0
    private Integer isRead;
    //创建时间
    private Date createTime = new Date();
}
