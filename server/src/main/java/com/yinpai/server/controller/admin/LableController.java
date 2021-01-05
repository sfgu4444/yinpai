package com.yinpai.server.controller.admin;

import com.yinpai.server.domain.entity.Lable;
import com.yinpai.server.domain.entity.admin.Message;
import com.yinpai.server.service.LableService;
import com.yinpai.server.thread.threadlocal.LoginAdminThreadLocal;
import com.yinpai.server.utils.PageUtil;
import com.yinpai.server.utils.ResultUtil;
import com.yinpai.server.vo.admin.ResultVO;
import com.yinpai.server.vo.admin.SaveWorkVo;
import lombok.extern.slf4j.Slf4j;
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
 * @description: 标签管理
 * @author: liuzhenda
 * @create: 2021/01/05 16:40
 */
@Slf4j
@Controller
@RequestMapping("/admin/lable")
public class LableController {

    private final LableService lableService;

    public LableController(LableService lableService) {
        this.lableService = lableService;
    }

    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size,
                             Map<String, Object> map) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest request = PageRequest.of(page - 1, size, sort);
        Page<Message> list = lableService.findAll(request);
        map.put("info", list);
        map.put("html", PageUtil.pageHtml(list.getTotalElements(), page, size));
        return new ModelAndView("lable/list", map);
    }

    @GetMapping("/add")
    public ModelAndView add() {
        return new ModelAndView("lable/add");
    }

    @PostMapping("/add")
    @ResponseBody
    public ResultVO add(Lable lable) {
        lableService.add(lable);
        return ResultUtil.success("保存成功");
    }

    @GetMapping("/edit")
    public ModelAndView edit(@RequestParam Integer id) {
        Map<String, Object> map = new HashMap<>();
        Lable lable = lableService.findByIdNotNull(id);
        map.put("data", lable);
        return new ModelAndView("lable/edit", map);
    }

    @PostMapping("/edit")
    @ResponseBody
    public ResultVO editData(Lable lable) {
        lableService.edit(lable);
        return ResultUtil.success();
    }

}
