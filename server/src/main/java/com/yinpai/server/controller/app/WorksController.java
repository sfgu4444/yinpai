package com.yinpai.server.controller.app;

import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.BaseFilterDto;
import com.yinpai.server.domain.dto.fiter.WorksFilterDto;
import com.yinpai.server.service.WorksService;
import com.yinpai.server.vo.IndexWorksVo;
import com.yinpai.server.vo.WorkDetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 2:19 下午
 */
@RestController
@RequestMapping("/works")
@Api(tags = "作品相关接口")
public class WorksController {

    private final WorksService worksService;

    @Autowired
    public WorksController(WorksService worksService) {
        this.worksService = worksService;
    }

    @GetMapping("/search")
    @ApiOperation("搜索列表")
    public PageResponse<IndexWorksVo> search(BaseFilterDto baseFilterDto,
                                              @ApiParam("搜索词") @RequestParam("keyword") String keyword,
                                              @ApiParam("类型筛选：1图片 2视频") @RequestParam("type") Integer type) {
        WorksFilterDto dto = new WorksFilterDto();
        dto.setType(type);
        dto.setPageable(baseFilterDto.getSetPageable());
        dto.setKeyword(keyword);
        return worksService.index(dto);
    }

    @GetMapping("/index")
    @ApiOperation("首页列表")
    public PageResponse<IndexWorksVo> index(BaseFilterDto baseFilterDto,
                                             @ApiParam("类型筛选：1图片 2视频") @RequestParam("type") Integer type) {
        WorksFilterDto dto = new WorksFilterDto();
        dto.setType(type);
        dto.setPageable(baseFilterDto.getSetPageable());
        return worksService.index(dto);
    }

    @ApiOperation("作品详情列表")
    @GetMapping("/{workId}")
    public WorkDetailVo detail(@ApiParam("作品ID") @PathVariable Integer workId) {
        return worksService.detail(workId);
    }

}
