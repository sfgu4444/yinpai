package com.yinpai.server.controller.app;

import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.BaseFilterDto;
import com.yinpai.server.domain.dto.fiter.WorksFilterDto;
import com.yinpai.server.service.WorksService;
import com.yinpai.server.service.AdminService;
import com.yinpai.server.vo.AdminHomeProfileVo;
import com.yinpai.server.vo.IndexWorksVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/30 11:06 上午
 */
@RestController
@RequestMapping("/admin")
@Api(tags = "商家作者相关接口")
public class AdminController {

    private final AdminService adminService;

    private final WorksService worksService;

    @Autowired
    public AdminController(AdminService adminService, WorksService worksService) {
        this.adminService = adminService;
        this.worksService = worksService;
    }

    @GetMapping("/{adminId}/homeProfile")
    @ApiOperation("商家主页资料接口")
    public AdminHomeProfileVo adminHomeProfile(@ApiParam("商家作者ID") @PathVariable Integer adminId) {
        return adminService.adminHomeProfile(adminId);
    }

    @GetMapping("/{adminId}/homeWorks")
    @ApiOperation("商家主页作品列表接口")
    public PageResponse<IndexWorksVo> adminHomeWorks(BaseFilterDto baseFilterDto,
                                                     @ApiParam("商家作者ID") @PathVariable Integer adminId,
                                                     @ApiParam("类型筛选：1图片 2视频") @RequestParam("type") Integer type) {
        WorksFilterDto dto = new WorksFilterDto();
        dto.setAdminId(adminId);
        dto.setType(type);
        dto.setPageable(baseFilterDto.getSetPageable());
        return worksService.index(dto);
    }
}
