package com.yinpai.server.vo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/29 2:59 下午
 */
@Data
public class AdminWorksListVo {

    private Integer id;

    public Integer adminId;

    public String adminName;

    public String nickName;

    public String title;

    public String content;

    public Long visitCount = 0L;

    public Long collectionCount = 0L;

    public Long downloadCount = 0L;

    public Integer type;

    public String coverImageUrl;

    public Integer status;

    public Integer isFree;

    public Integer price;

    private Date createTime = new Date();

    private Date updateTime = new Date();

    public String lable;
}
