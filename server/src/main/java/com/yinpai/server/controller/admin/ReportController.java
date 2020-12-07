package com.yinpai.server.controller.admin;

import com.yinpai.server.domain.entity.admin.Admin;
import com.yinpai.server.service.ReportService;
import com.yinpai.server.utils.PageUtil;
import com.yinpai.server.vo.admin.AdminReportListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/21 10:03 下午
 */
@Controller("adminReportController")
@RequestMapping("/admin/report")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size,
                             Map<String, Object> map) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest request = PageRequest.of(page - 1, size, sort);
        HashMap<String, String> conditionMap = new HashMap<>();
        conditionMap.put("content", "like");
        Page<AdminReportListVo> list = reportService.findFilterAll(conditionMap, request);
        map.put("info", list);
        map.put("html", PageUtil.pageHtml(list.getTotalElements(), page, size));
        return new ModelAndView("report/list", map);
    }
}
