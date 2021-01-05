package com.yinpai.server.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @program server
 * @description: 标签
 * @author: liuzhenda
 * @create: 2021/01/05 16:42
 */
@Entity
@Data
@Table(name = "yp_lable")
public class Lable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String lableName;

    private Date createTime = new Date();

}
