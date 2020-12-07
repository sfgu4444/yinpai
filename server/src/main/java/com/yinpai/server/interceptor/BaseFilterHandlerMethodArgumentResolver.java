package com.yinpai.server.interceptor;

import com.yinpai.server.domain.dto.fiter.BaseFilterDto;
import com.yinpai.server.enums.PageRequestEnum;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/19 3:30 下午
 */
@Component
public class BaseFilterHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().isAssignableFrom(BaseFilterDto.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        String page = nativeWebRequest.getParameter(PageRequestEnum.PAGE.getCode());
        String size = nativeWebRequest.getParameter(PageRequestEnum.SIZE.getCode());
        String sortDirection = nativeWebRequest.getParameter(PageRequestEnum.sortDirection.getCode());
        String sortDirectionField = nativeWebRequest.getParameter(PageRequestEnum.sortDirectionField.getCode());
        BaseFilterDto baseFilterDto = new BaseFilterDto();
        baseFilterDto.setPage(StringUtils.isEmpty(page) ? 1 : Integer.parseInt(page));
        baseFilterDto.setSize(StringUtils.isEmpty(size) ? 10 : Integer.parseInt(size));
        baseFilterDto.setSortDirection(StringUtils.isEmpty(sortDirection) ? "DESC" : sortDirection);
        baseFilterDto.setSortDirectionField(StringUtils.isEmpty(sortDirectionField) ? "id" : sortDirectionField);
        baseFilterDto.getSetPageable();
        return baseFilterDto;
    }
}
