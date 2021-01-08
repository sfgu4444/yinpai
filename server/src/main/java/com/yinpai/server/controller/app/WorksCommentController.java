package com.yinpai.server.controller.app;

import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.BaseFilterDto;
import com.yinpai.server.domain.dto.fiter.WorksCommentFilterDto;
import com.yinpai.server.domain.repository.ReportCommonRepository;
import com.yinpai.server.domain.repository.ReportRepository;
import com.yinpai.server.log.WebLog;
import com.yinpai.server.service.WorksCommentService;
import com.yinpai.server.vo.WorksCommentListVo;
import com.yinpai.server.vo.admin.ReportVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final ReportCommonRepository reportCommonRepository;

    @Autowired
    public WorksCommentController(WorksCommentService worksCommentService, ReportCommonRepository reportCommonRepository) {
        this.worksCommentService = worksCommentService;
        this.reportCommonRepository = reportCommonRepository;
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
    @WebLog(description = "发表评论")
    public Integer addComment(@ApiParam("作品ID") @RequestParam Integer workId,
                              @ApiParam("评论内容") @RequestParam String content) {
        return worksCommentService.addComment(workId, content);
    }

    @PersistenceContext //注入的是实体管理器,执行持久化操作
    EntityManager entityManager;

    @PostMapping("/getWorksLook")
    @ResponseBody
    @ApiOperation("获取统计")
    public Map<String, Object> getWorksLook() {

        Map<String,Object> map = new HashMap<>();

        String startTime = "2010-12-14 19:53:27";
        String endTime = "2030-12-14 19:53:27";
        // 登陆统计
        String yp_login_log = "yp_login_log";
        // 交易次数 和 金额
        String yp_user_order = "yp_user_order";
        // 点击量
        String yp_works_look_log = "yp_works_look_log";
        // 发布量
        String yp_works = "yp_works";
        // 注册量
        String yp_user = "yp_user";

        // 登陆统计
        Long reportVoLogin = reportCommonRepository.getYpLoginLogCount(startTime, endTime);
        List<Map<String,Object>> reportVoListLogin = reportCommonRepository.getYpLoginLogByDay( startTime, endTime);
        // 交易额
        Long reportVoOrder = reportCommonRepository.getYpUserOrderCountMoney(startTime, endTime);
        List<Map<String,Object>> reportVoListOrder = reportCommonRepository.getYpUserOrderDayMoney(startTime, endTime);
        // 交易次数
        Long reportVoOrderCount = reportCommonRepository.getYpUserOrderCount( startTime, endTime);
        List<Map<String,Object>> reportVoListOrderCount = reportCommonRepository.getYpUserOrderByDay( startTime, endTime);
        // 点击量
        Long reportVoLook = reportCommonRepository.getYpWorksLookLogCount( startTime, endTime, 3);
        List<Map<String,Object>> reportVoListLook = reportCommonRepository.getYpWorksLookLogByDay(startTime, endTime, 3);
        // 发布量
        Long reportVoWorks = reportCommonRepository.getYpWorksCount( startTime, endTime, 3);
        List<Map<String,Object>> reportVoListWorks = reportCommonRepository.getYpWorksByDay( startTime, endTime, 3);
        // 注册量
        Long reportVoUser = reportCommonRepository.getYpUserCount( startTime, endTime);
        List<Map<String,Object>> reportVoListUser = reportCommonRepository.getYpUserByDay(startTime, endTime);

        map.put("reportVoLogin",reportVoLogin);
        map.put("reportVoListLogin",reportVoListLogin);
        map.put("reportVoOrder",reportVoOrder);
        map.put("reportVoListOrder",reportVoListOrder);
        map.put("reportVoOrderCount",reportVoOrderCount);
        map.put("reportVoListOrderCount",reportVoListOrderCount);
        map.put("reportVoLook",reportVoLook);
        map.put("reportVoListLook",reportVoListLook);
        map.put("reportVoWorks",reportVoWorks);
        map.put("reportVoListWorks",reportVoListWorks);
        map.put("reportVoUser",reportVoUser);
        map.put("reportVoListUser",reportVoListUser);

        return map;
    }
}
