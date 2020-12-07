package com.yinpai.server.advice;

import com.yinpai.server.annotation.IgnoreCommonResponse;
import com.yinpai.server.domain.dto.CommonResponse;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2019-06-21 19:33
 */
@RestControllerAdvice(
    basePackages = {
        "com.yinpai.server.controller.app",
    })
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {

    @Override
    @SuppressWarnings("all")
    public boolean supports(
            MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {

        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreCommonResponse.class)) {
            return false;
        }

        if (methodParameter.getMethod().isAnnotationPresent(IgnoreCommonResponse.class)) {
            return false;
        }

        return true;
    }

    @Nullable
    @Override
    @SuppressWarnings("all")
    public Object beforeBodyWrite(
        @Nullable Object o,
        MethodParameter methodParameter,
        MediaType mediaType,
        Class<? extends HttpMessageConverter<?>> aClass,
        ServerHttpRequest serverHttpRequest,
        ServerHttpResponse serverHttpResponse) {
        CommonResponse<Object> response = new CommonResponse<>();
        response.setCode(HttpStatus.OK.value());
        response.setMsg("请求成功");
        LoginUserThreadLocal.remove();
        if (null == o) {
            return response;
        } else if (o instanceof CommonResponse) {
            response = (CommonResponse<Object>) o;
        } else {
            response.setData(o);
        }
        return response;
    }
}
