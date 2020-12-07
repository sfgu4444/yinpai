package com.yinpai.server.controller.app;

import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.entity.Report;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.service.ReportService;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/21 6:59 下午
 */
@RestController
@RequestMapping("/report")
@Api(tags = "举报相关")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/add")
    @ApiOperation("举报")
    public void add(@ApiParam("举报类型作品ID") @RequestParam("workId") Integer workId,
                    @ApiParam("举报类型，直接传汉字") @RequestParam("type") String type,
                    @ApiParam("举报内容") @RequestParam("content") String content) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        Report report = new Report();
        report.setUserId(userInfoDto.getUserId());
        report.setWorkId(workId);
        report.setType(type);
        report.setContent(content);
        report.setCreateTime(new Date());
        reportService.save(report);
    }
}
