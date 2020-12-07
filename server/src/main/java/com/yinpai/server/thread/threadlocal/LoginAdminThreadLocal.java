package com.yinpai.server.thread.threadlocal;

import com.yinpai.server.domain.dto.LoginAdminInfoDto;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/29 11:57 上午
 */
public class LoginAdminThreadLocal {

    private static InheritableThreadLocal<LoginAdminInfoDto> threadLocal = new InheritableThreadLocal<>();

    public static LoginAdminInfoDto get() {
        return threadLocal.get();
    }

    public static void set(LoginAdminInfoDto v) {
        threadLocal.set(v);
    }

    public static void remove() {
        threadLocal.remove();
    }

}
