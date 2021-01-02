package com.yinpai.server.domain.entity.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 4:35 下午
 */
@Entity
@Data
@Table(name = "yp_admin")
public class Admin{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String adminName;

    private String adminPass;

    private String nickName;

    private String avatarUrl;

    private Date lastTime;

    private String lastIp;

    private String backgroundUrl;

    private Integer adminStatus;

    private Integer monthPayPrice;

    private Integer quarterPayPrice;

    private Integer yearPayPrice;

    private Integer isAudit;

    private Date createTime = new Date();

    private Date updateTime = new Date();

    private Integer money;

}
