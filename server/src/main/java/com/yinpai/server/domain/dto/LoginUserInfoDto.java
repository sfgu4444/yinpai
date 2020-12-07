package com.yinpai.server.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/17 1:29 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginUserInfoDto {

    private Integer userId;
}
