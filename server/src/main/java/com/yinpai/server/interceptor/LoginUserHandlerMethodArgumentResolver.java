package com.yinpai.server.interceptor;

import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.service.UserService;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/17 1:28 下午
 */
@Component
public class LoginUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserService userService;

    @Autowired
    public LoginUserHandlerMethodArgumentResolver(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().isAssignableFrom(LoginUserInfoDto.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        String token = nativeWebRequest.getHeader("token");
        LoginUserInfoDto build = userService.tokenToUserInfo(token, true);
        LoginUserThreadLocal.set(build);
        return build;
    }
}
