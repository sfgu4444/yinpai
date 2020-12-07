package com.yinpai.server.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2019-05-17 17:41
 */
@Component
public final class MySpringUtils implements ServletContextListener {

    private static WebApplicationContext springContext = null;

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        if (springContext == null) {
            springContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("unchecked")
    public static  <T> T getBean(String name) {
        return (T) springContext.getBean(name);
    }

    public static  <T> T getBean(Class<T> clazz) {
        return springContext.getBean(clazz);
    }
}
