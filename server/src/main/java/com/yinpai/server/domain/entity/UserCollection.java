package com.yinpai.server.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/5 10:00 下午
 */
@Data
@Entity
@Table(name = "yp_user_collection")
public class UserCollection {

    private static final long serialVersionUID = -4185341102986875344L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer workId;

    private Integer userId;

    private Date createTime;

    private Date updateTime;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workId", referencedColumnName = "id", updatable = false, insertable = false)
    private Works works;
}
