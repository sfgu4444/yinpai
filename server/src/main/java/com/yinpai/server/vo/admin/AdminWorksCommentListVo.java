package com.yinpai.server.vo.admin;

import lombok.Data;

import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/6 4:55 下午
 */
@Data
public class AdminWorksCommentListVo {

    private Integer id;

    public String title;

    public String coverImageUrl;

    private String nickName;

    private String avatarUrl;

    private String content;

    private String replyContent;

    private Date createTime;

    private Date replyTime;
}
