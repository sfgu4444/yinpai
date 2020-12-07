package com.yinpai.server.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/30 12:33 下午
 */
@Entity
@Data
@Table(name = "yp_works_comment")
public class WorksComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer workId;

    private Integer userId;

    private String content;

    private Integer adminId;

    private String replyContent;

    private Date createTime;

    private Date replyTime;
}
