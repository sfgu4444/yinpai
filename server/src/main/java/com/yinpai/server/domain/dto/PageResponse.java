package com.yinpai.server.domain.dto;

import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/19 3:04 下午
 */
@Data
public class PageResponse<T>{

    private static final long serialVersionUID = -3720998571176536865L;

    @SuppressWarnings("rawtypes")
    public static final PageResponse EMPTY = new PageResponse<>();

    private List<T> content = new ArrayList<>();

    private long totalElements;

    private int pageNumber;

    private int pageSize;

    public PageResponse() {
    }

    public PageResponse(List<T> content, Pageable pageable, long total) {
        this.content = content;
        this.totalElements = total;
        this.pageNumber = pageable.getPageNumber() + 1;
        this.pageSize = pageable.getPageSize();
    }

    public static <T> PageResponse<T> of(List<T> content, Pageable pageable, long total) {
        return new PageResponse<>(content, pageable, total);
    }

}
