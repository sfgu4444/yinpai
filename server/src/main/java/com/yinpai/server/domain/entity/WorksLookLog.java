package com.yinpai.server.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "yp_works_look_log")
@Data
public class WorksLookLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer worksId;

    private Integer type;

    private Date createTime = new Date();

}
