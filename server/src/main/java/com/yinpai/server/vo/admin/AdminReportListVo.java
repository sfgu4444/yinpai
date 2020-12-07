package com.yinpai.server.vo.admin;

import lombok.Data;

import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/21 10:07 下午
 */
@Data
public class AdminReportListVo {

    private Integer id;

    private String nickName;

    private String title;

    private String type;

    private String content;

    private Date createTime;

    private Integer status;

    private Integer workId;
}
