package com.yinpai.server.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/6 5:54 下午
 */
@Data
@Entity
@Table(name = "yp_user_download_record")
public class UserDownloadRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer workId;

    private Integer userId;

    private Date createTime;
}
