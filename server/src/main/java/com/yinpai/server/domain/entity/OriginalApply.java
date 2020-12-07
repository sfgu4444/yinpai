package com.yinpai.server.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/6 12:36 上午
 */
@Data
@Entity
@Table(name = "yp_original_apply")
public class OriginalApply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String contactWay;

    private String wx;

    private String qq;

    private String email;

    private String content;

    private Date createTime;
}
