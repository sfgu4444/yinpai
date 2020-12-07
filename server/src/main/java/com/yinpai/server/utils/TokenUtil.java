package com.yinpai.server.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.yinpai.server.domain.entity.User;

import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2019-11-13 11:45
 */
public class TokenUtil {

    public static String getToken(User user) {
        return JWT.create()
                .withClaim("userId", user.getId())
                .withNotBefore(new Date())//设置 载荷 定义在什么时间之前，该jwt都是不可用的.
                .withAudience("ALL")
                .withIssuedAt(new Date())
                .withExpiresAt(DateUtil.getNowToNextDayDate(7))
                .sign(Algorithm.HMAC256("secret"));//签名 Signature
    }

    public static void main(String[] args) {
        System.out.println(JWT.create()
                .withClaim("userId", 1)
                .withNotBefore(new Date())//设置 载荷 定义在什么时间之前，该jwt都是不可用的.
                .withAudience("ALL")
                .withIssuedAt(new Date())
                .withExpiresAt(DateUtil.getNowToNextDayDate(7))
                .sign(Algorithm.HMAC256("secret")));
    }
}
