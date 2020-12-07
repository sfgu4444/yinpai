package com.yinpai.server.config;

import com.yinpai.server.interceptor.BaseFilterHandlerMethodArgumentResolver;
import com.yinpai.server.interceptor.LoginUserHandlerMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/29 1:09 下午
 */
@Configuration
public class ServletContextConfig extends WebMvcConfigurationSupport {

    private final LoginUserHandlerMethodArgumentResolver loginUserHandlerMethodArgumentResolver;

    private final BaseFilterHandlerMethodArgumentResolver baseFilterHandlerMethodArgumentResolver;

    @Autowired
    public ServletContextConfig(LoginUserHandlerMethodArgumentResolver loginUserHandlerMethodArgumentResolver, BaseFilterHandlerMethodArgumentResolver baseFilterHandlerMethodArgumentResolver) {
        this.loginUserHandlerMethodArgumentResolver = loginUserHandlerMethodArgumentResolver;
        this.baseFilterHandlerMethodArgumentResolver = baseFilterHandlerMethodArgumentResolver;
    }

    /**
     * 发现如果继承了WebMvcConfigurationSupport，则在yml中配置的相关内容会失效。
     * 需要重新指定静态资源
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        super.addResourceHandlers(registry);
    }

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new MappingJackson2HttpMessageConverter());
        converters.add(1, new ResourceHttpMessageConverter());
    }

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(loginUserHandlerMethodArgumentResolver);
        argumentResolvers.add(baseFilterHandlerMethodArgumentResolver);
    }

    /**
     * 配置servlet处理
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST", "OPTIONS").allowCredentials(true).allowedHeaders("*");
    }
}
