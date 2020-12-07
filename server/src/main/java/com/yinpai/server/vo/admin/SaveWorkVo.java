package com.yinpai.server.vo.admin;

import lombok.Data;

import java.util.List;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/29 11:44 上午
 */
@Data
public class SaveWorkVo {

    private Integer id;

    public Integer userId;

    public String title;

    public String content;

    public Integer type;

    public String coverImageUrl;

    public Integer isFree;

    public Integer price;

    public String goodsImagesDetail;

    public String videoDetail;

    public String folder;
}
