package com.yinpai.server.interceptor;

import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.service.UserService;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.*;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/18 5:28 下午
 */
@Component
public class WebFilter implements Filter {

    private final UserService userService;

    @Autowired
    public WebFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String origin = request.getHeader("Origin");
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type,Content-Length, Authorization,Origin,Accept,X-Requested-With,Token,client");
        response.setHeader(ACCESS_CONTROL_ALLOW_METHODS, "PUT,POST,GET,DELETE,PATCH,OPTIONS");
        response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        response.setHeader(ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Type,Content-Length,Content-Disposition");
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            response.setStatus(HttpStatus.OK.value());
            return;
        }
        String token = request.getHeader("token");
        LoginUserInfoDto build = userService.tokenToUserInfo(token, false);
        if (build != null) {
            LoginUserThreadLocal.set(build);
        }
        filterChain.doFilter(servletRequest, servletResponse);

    }




}
