package com.yinpai.server.controller.admin;

import com.yinpai.server.log.WebLog;
import com.yinpai.server.service.UserAdviceService;
import com.yinpai.server.utils.PageUtil;
import com.yinpai.server.utils.ResultUtil;
import com.yinpai.server.vo.admin.Advice;
import com.yinpai.server.vo.admin.ResultVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @program server
 * @description: 建议反馈
 * @author: liuzhenda
 * @create: 2021/01/10 12:47
 */
@RestController
@RequestMapping("/admin/advice")
public class AdviceController {

    private final UserAdviceService userAdviceService;

    public AdviceController(UserAdviceService userAdviceService) {
        this.userAdviceService = userAdviceService;
    }

    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size,
                             Map<String, Object> map) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest request = PageRequest.of(page - 1, size, sort);
        Page<Advice> list = userAdviceService.findFilterAll(request);
        map.put("info", list);
        map.put("html", PageUtil.pageHtml(list.getTotalElements(), page, size));
        return new ModelAndView("advice/list", map);
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultVO delete(@RequestParam Integer id) {
        userAdviceService.delete(id);
        return ResultUtil.success("删除成功");
    }
}
