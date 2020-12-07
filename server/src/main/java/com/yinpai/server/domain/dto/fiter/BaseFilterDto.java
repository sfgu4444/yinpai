package com.yinpai.server.domain.dto.fiter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/19 3:18 下午
 */
@Data
public class BaseFilterDto {

    /**
     * 页面
     */
    @ApiModelProperty(value = "页数", example = "1")
    private Integer page;

    /**
     * 数据条数
     */
    @ApiModelProperty(value = "页码", example = "10")
    private Integer size;

    /**
     * 排序方式
     */
    @ApiModelProperty(value = "排序方式", example = "desc")
    private String sortDirection;

    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段", example = "id")
    private String sortDirectionField;

    private Pageable pageable;

    public Pageable getSetPageable() {
        this.page = this.page == null ? 1 : this.page;
        this.size = this.size == null ? 10 : this.size;
        this.sortDirection = this.sortDirection == null ? "desc" : this.sortDirection;
        this.sortDirectionField = this.sortDirectionField == null ? "id" : this.sortDirectionField;
        this.pageable = PageRequest.of(this.page - 1, this.size, Sort.by(Sort.Direction.fromString(this.sortDirection), this.sortDirectionField));
        return this.pageable;
    }

    public Pageable getPageableNotSort() {
        return PageRequest.of(this.page - 1, this.size);
    }
}
