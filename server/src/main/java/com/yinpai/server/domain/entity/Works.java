package com.yinpai.server.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 2:13 下午
 */
@Data
@Entity
@Table(name = "yp_works")
public class Works{

    private static final long serialVersionUID = 5167295698192901380L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    public Integer adminId;

    public String title;

    public String content;

    public Long visitCount = 0L;

    public Long collectionCount = 0L;

    public Integer type;

    public String coverImageUrl;

    public Integer status;

    public Integer isFree;

    public Integer price;

    public String zipUrl;

    private Long lookCount = 0L;

    public Date createTime = new Date();

    public Date updateTime = new Date();

}
