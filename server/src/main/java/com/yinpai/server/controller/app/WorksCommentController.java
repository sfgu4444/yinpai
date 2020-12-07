package com.yinpai.server.controller.app;

import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.BaseFilterDto;
import com.yinpai.server.domain.dto.fiter.WorksCommentFilterDto;
import com.yinpai.server.domain.dto.fiter.WorksFilterDto;
import com.yinpai.server.service.WorksCommentService;
import com.yinpai.server.vo.IndexWorksVo;
import com.yinpai.server.vo.WorksCommentListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/30 9:26 下午
 */
@RestController
@RequestMapping("/works/comment")
@Api(tags = "作品评论")
public class WorksCommentController {

    private final WorksCommentService worksCommentService;

    @Autowired
    public WorksCommentController(WorksCommentService worksCommentService) {
        this.worksCommentService = worksCommentService;
    }

    @GetMapping
    @ApiOperation("作品评论列表")
    public PageResponse<WorksCommentListVo> worksCommentList(BaseFilterDto baseFilterDto,
                                                             @ApiParam("作品ID") @RequestParam Integer workId) {
        WorksCommentFilterDto dto = new WorksCommentFilterDto();
        dto.setWorkId(workId);
        dto.setPageable(baseFilterDto.getSetPageable());
        return worksCommentService.commentList(dto);
    }

    @PostMapping
    @ApiOperation("发表评论")
    public Integer addComment(@ApiParam("作品ID") @RequestParam Integer workId,
                              @ApiParam("评论内容") @RequestParam String content) {
        return worksCommentService.addComment(workId, content);
    }
}
