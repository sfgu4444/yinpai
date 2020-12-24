package com.yinpai.server.controller.admin;

import com.yinpai.server.domain.repository.ReportCommonRepository;
import com.yinpai.server.service.WorksCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program server
 * @description: 数据统计
 * @author: liuzhenda
 * @create: 2020/12/23 19:38
 */
@Controller("adminDataController")
@RequestMapping("/admin/data")
public class DataController {


    private final ReportCommonRepository reportCommonRepository;

    @Autowired
    public DataController(WorksCommentService worksCommentService, ReportCommonRepository reportCommonRepository) {
        this.reportCommonRepository = reportCommonRepository;
    }

    @GetMapping("/report")
    public ModelAndView welcome(){

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
        return new ModelAndView("data/report",map);
    }
}
