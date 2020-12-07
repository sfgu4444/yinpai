package com.yinpai.server.service;

import com.yinpai.server.domain.entity.Report;
import com.yinpai.server.domain.entity.User;
import com.yinpai.server.domain.entity.Works;
import com.yinpai.server.domain.entity.admin.Admin;
import com.yinpai.server.domain.repository.ReportRepository;
import com.yinpai.server.utils.ProjectUtil;
import com.yinpai.server.vo.admin.AdminReportListVo;
import com.yinpai.server.vo.admin.AdminWorksListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/21 6:59 下午
 */
@Service
public class ReportService {

    private final ReportRepository reportRepository;

    private final WorksService worksService;

    private final UserService userService;

    @Autowired
    public ReportService(ReportRepository reportRepository, @Lazy WorksService worksService, @Lazy UserService userService) {
        this.reportRepository = reportRepository;
        this.worksService = worksService;
        this.userService = userService;
    }

    public Report save(Report report) {
        return reportRepository.save(report);
    }

    public Page<AdminReportListVo> findFilterAll(HashMap<String, String> conditionMap, PageRequest request) {
        Page<Report> reports = reportRepository.findAll(ProjectUtil.getSpecification(conditionMap), request);
        List<AdminReportListVo> voList = new ArrayList<>();
        reports.forEach(w -> {
            AdminReportListVo vo = new AdminReportListVo();
            vo.setId(w.getId());
            User user = userService.findById(w.getUserId());
            if (user != null) {
                vo.setNickName(user.getNickName());
            }
            Works works = worksService.findById(w.getWorkId());
            if (works!=null) {
                vo.setTitle(works.getTitle());
                vo.setStatus(works.getStatus());
                vo.setWorkId(works.getId());
            }
            vo.setType(w.getType());
            vo.setContent(w.getContent());
            vo.setCreateTime(w.getCreateTime());
voList.add(vo);
        });
        return new PageImpl<>(voList, reports.getPageable(), reports.getTotalElements());
    }
}
