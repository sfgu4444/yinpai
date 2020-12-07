package com.yinpai.server.thread.threadlocal;


import com.yinpai.server.domain.dto.LoginUserInfoDto;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/18 4:56 下午
 */
public class LoginUserThreadLocal {

    private static InheritableThreadLocal<LoginUserInfoDto> threadLocal = new InheritableThreadLocal<>();

    public static LoginUserInfoDto get() {
        return threadLocal.get();
    }

    public static void set(LoginUserInfoDto v) {
        threadLocal.set(v);
    }

    public static void remove() {
        threadLocal.remove();
    }

}
