package com.yinpai.server.controller.admin;

import com.yinpai.server.domain.dto.LoginAdminInfoDto;
import com.yinpai.server.domain.entity.admin.Admin;
import com.yinpai.server.exception.ProjectException;
import com.yinpai.server.service.AdminService;
import com.yinpai.server.thread.threadlocal.LoginAdminThreadLocal;
import com.yinpai.server.utils.PageUtil;
import com.yinpai.server.utils.ResultUtil;
import com.yinpai.server.vo.admin.AdminAddForm;
import com.yinpai.server.vo.admin.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 4:55 下午
 */
@Controller("adminAdminController")
@RequestMapping("/admin/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size,
                             Map<String, Object> map) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest request = PageRequest.of(page - 1, size, sort);
        HashMap<String, String> conditionMap = new HashMap<>();
        conditionMap.put("adminName", "like");
        Page<Admin> list = adminService.findFilterAll(conditionMap, request);
        map.put("info", list);
        map.put("html", PageUtil.pageHtml(list.getTotalElements(), page, size));
        return new ModelAndView("admin/list", map);
    }

    @GetMapping("/add")
    public ModelAndView add() {
        return new ModelAndView("admin/add");
    }

    @PostMapping("/add")
    @ResponseBody
    public ResultVO add(@Valid AdminAddForm adminAddForm,
                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ProjectException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        if (!adminAddForm.getAdminPass().equals(adminAddForm.getAdminCheckPass())) {
            throw new ProjectException("两次密码不一致");
        }
        adminService.add(adminAddForm);
        return ResultUtil.success("商家添加成功");
    }

    @GetMapping("/edit")
    public ModelAndView edit(@RequestParam Integer id,
                             Map<String, Object> map) {
        Admin admin = adminService.findByIdNotNull(id);
        map.put("data", admin);
        return new ModelAndView("admin/edit", map);
    }

    @PostMapping("/edit")
    @ResponseBody
    public ResultVO editData(AdminAddForm adminAddForm) {
        if (adminAddForm.getAdminPass() != null && !adminAddForm.getAdminPass().equals(adminAddForm.getAdminCheckPass())) {
            throw new ProjectException("两次密码不一致");
        }
        adminService.edit(adminAddForm);
        return ResultUtil.success("修改商家信息成功");
    }

    @GetMapping("/audit")
    public ModelAndView audit(@RequestParam Integer id,
                              Map<String, Object> map) {
        Admin admin = adminService.findByIdNotNull(id);
        map.put("data", admin);
        return new ModelAndView("admin/audit", map);
    }

    @PostMapping("/audit")
    @ResponseBody
    public ResultVO auditData(@RequestParam Integer id,
                              @RequestParam Integer isAudit) {
        adminService.changeAudit(id, isAudit);
        return ResultUtil.success();
    }

    @PostMapping("/status")
    @ResponseBody
    public ResultVO changeAdminStatus(@RequestParam Integer id) {
        Integer adminStatus = adminService.changeAdminStatus(id);
        return ResultUtil.successData(adminStatus);
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultVO delete(@RequestParam Integer id) {
        adminService.delete(id);
        return ResultUtil.success("商家删除成功");
    }
}
