package com.yinpai.server.controller.app;

import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.BaseFilterDto;
import com.yinpai.server.service.UserDownloadRecordService;
import com.yinpai.server.service.WorksService;
import com.yinpai.server.vo.UserDownloadListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/6 5:58 下午
 */
@RestController
@RequestMapping("/user/download")
@Api(tags = "用户下载相关")
public class UserDownloadController {

    private final WorksService worksService;

    private final UserDownloadRecordService userDownloadRecordService;

    @Autowired
    public UserDownloadController(WorksService worksService, UserDownloadRecordService userDownloadRecordService) {
        this.worksService = worksService;
        this.userDownloadRecordService = userDownloadRecordService;
    }

    @ApiOperation("用户作品下载")
    @GetMapping("/url")
    public String downloadUrl(@ApiParam("作品ID") @RequestParam("workId") Integer workId) {
        return worksService.downloadUrl(workId);
    }

    @GetMapping("/list")
    @ApiOperation("用户下载记录列表")
    public PageResponse<UserDownloadListVo> list(BaseFilterDto baseFilterDto) {
        return userDownloadRecordService.findAll(baseFilterDto);
    }

}
