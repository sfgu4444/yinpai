package com.yinpai.server.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 2:53 下午
 */
@Entity
@Table(name = "yp_works_images")
@Data
public class WorksResources{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    private Integer workId;

    private Integer type;

    private String fileUrl;

    private Date createTime = new Date();

    private Date updateTime = new Date();
}
