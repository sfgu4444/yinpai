package com.yinpai.server.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/18 11:14 上午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoGpsInfoDto {

    /**
     * gps位置标识
     */
    private String member;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;
}
