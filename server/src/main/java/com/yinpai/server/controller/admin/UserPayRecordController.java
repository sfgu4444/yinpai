package com.yinpai.server.controller.admin;

import com.yinpai.server.domain.entity.User;
import com.yinpai.server.domain.entity.UserPayRecord;
import com.yinpai.server.service.UserPayRecordService;
import com.yinpai.server.service.UserService;
import com.yinpai.server.utils.PageUtil;
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
 * @program server
 * @description: 用户支付记录
 * @author: liuzhenda
 * @create: 2021/01/04 11:57
 */
@Controller("adminPayRecordController")
@RequestMapping("/admin/pay")
public class UserPayRecordController {
    private final UserPayRecordService userPayRecordService;

    @Autowired
    public UserPayRecordController(UserPayRecordService userPayRecordService) {
        this.userPayRecordService = userPayRecordService;
    }

    @GetMapping("/record")
    public ModelAndView list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size,
                             Map<String, Object> map) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest request = PageRequest.of(page - 1, size, sort);
        HashMap<String, String> conditionMap = new HashMap<>();
        conditionMap.put("userName", "like");
        Page<UserPayRecord> list = userPayRecordService.findFilterAll(conditionMap, request);
        map.put("info", list);
        map.put("html", PageUtil.pageHtml(list.getTotalElements(), page, size));
        return new ModelAndView("user/record", map);
    }



}
