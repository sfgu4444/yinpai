package com.yinpai.server.controller.admin;

import com.yinpai.server.domain.entity.admin.Admin;
import com.yinpai.server.domain.entity.admin.Withdrawal;
import com.yinpai.server.domain.repository.admin.AdminRepository;
import com.yinpai.server.exception.ProjectException;
import com.yinpai.server.service.AdminService;
import com.yinpai.server.service.MessageService;
import com.yinpai.server.service.WithdrawlService;
import com.yinpai.server.thread.threadlocal.LoginAdminThreadLocal;
import com.yinpai.server.utils.PageUtil;
import com.yinpai.server.utils.ResultUtil;
import com.yinpai.server.vo.admin.ResultVO;
import com.yinpai.server.vo.admin.SaveWorkVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @program server
 * @description: 提现
 * @author: liuzhenda
 * @create: 2021/01/06 14:04
 */

@Slf4j
@Controller
@RequestMapping("/admin/withdrawal")
public class WithdrawalController {

    private final WithdrawlService withdrawlService;

    private final MessageService messageService;

    private final AdminRepository adminRepository;

    private final AdminService adminService;

    @Autowired
    public WithdrawalController(WithdrawlService withdrawlService, MessageService messageService, AdminRepository adminRepository, AdminService adminService) {
        this.withdrawlService = withdrawlService;
        this.messageService = messageService;
        this.adminRepository = adminRepository;
        this.adminService = adminService;
    }

    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size,
                             Map<String, Object> map) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest request = PageRequest.of(page - 1, size, sort);
        Page<Withdrawal> list = withdrawlService.findFilterAll(request);
        map.put("info", list);
        map.put("html", PageUtil.pageHtml(list.getTotalElements(), page, size));
        map.put("superAdmin", LoginAdminThreadLocal.get().isSuperAdmin());
        map.put("adminList",messageService.adminList());
        return new ModelAndView("withdrawal/list", map);
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultVO delete(@RequestParam Integer id) {
        try {
            withdrawlService.delete(id);
        }catch (Exception e){
            return ResultUtil.error(1,e.getMessage());
        }
        return ResultUtil.success("删除成功");
    }

    @GetMapping("/add")
    public ModelAndView add() {
        Map<String, Object> map = new HashMap<>();
       Admin admin = adminService.findById(LoginAdminThreadLocal.get().getAdminId());
       if(StringUtils.isBlank(admin.getNameBandk())||
               StringUtils.isBlank(admin.getBandk())||
               StringUtils.isBlank(admin.getBandkAccount().toString())){
           throw new ProjectException("请绑定信息");
       }
        map.put("info",admin);
        return new ModelAndView("withdrawal/add",map);
    }


    @PostMapping("/add")
    @ResponseBody
    public ResultVO add(Withdrawal withdrawal) {
        withdrawlService.add(withdrawal);
        return ResultUtil.success("提交成功");
    }

    @PostMapping("/edit")
    @ResponseBody
    public ResultVO editData(@RequestParam Integer id) {
        Withdrawal withd = withdrawlService.findByIdNotNull(id);
        withdrawlService.edit(withd);
        return ResultUtil.success();
    }

    @GetMapping("/bind")
    public ModelAndView bind() {
        Map<String, Object> map = new HashMap<>();
        map.put("info",adminService.findById(LoginAdminThreadLocal.get().getAdminId()));
        return new ModelAndView("withdrawal/bind",map);
    }

    @PostMapping("/bind")
    @ResponseBody
    public ResultVO bind(Admin admin) {
        withdrawlService.bind(admin);
        return ResultUtil.success("保存成功");
    }
}
