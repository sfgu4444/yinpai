package com.yinpai.server.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/21 6:24 下午
 */
@Entity
@Data
@Table(name = "yp_user_pay_record")
public class UserPayRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private Integer adminId;

    private Integer type;

    private Integer money;

    private Date createTime;

}
