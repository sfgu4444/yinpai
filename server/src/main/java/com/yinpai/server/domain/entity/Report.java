package com.yinpai.server.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/21 6:57 下午
 */
@Entity
@Table(name = "yp_report")
@Data
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private Integer workId;

    private String type;

    private String content;

    private Date createTime;
}
