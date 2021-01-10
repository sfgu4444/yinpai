package com.yinpai.server.controller.admin;

import com.yinpai.server.domain.entity.Lable;
import com.yinpai.server.domain.entity.OriginalApply;
import com.yinpai.server.domain.entity.User;
import com.yinpai.server.domain.entity.Works;
import com.yinpai.server.service.OriginalApplyService;
import com.yinpai.server.utils.PageUtil;
import com.yinpai.server.utils.ResultUtil;
import com.yinpai.server.vo.admin.ResultVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program server
 * @description: 原创入驻
 * @author: liuzhenda
 * @create: 2021/01/10 23:01
 */
@RestController
@RequestMapping("/admin/origin")
public class OriginalApplyController {

    private final OriginalApplyService originalApplyService;

    public OriginalApplyController(OriginalApplyService originalApplyService) {
        this.originalApplyService = originalApplyService;
    }

    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size,
                             Map<String, Object> map) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest request = PageRequest.of(page - 1, size, sort);
        HashMap<String, String> conditionMap = new HashMap<>();
        conditionMap.put("contactWay", "like");
        Page<OriginalApply> list = originalApplyService.findFilterAll(conditionMap, request);
        map.put("info", list);
        map.put("html", PageUtil.pageHtml(list.getTotalElements(), page, size));
        return new ModelAndView("origin/list", map);
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultVO delete(@RequestParam Integer id) {
        originalApplyService.delete(id);
        return ResultUtil.success("删除成功");
    }

    @GetMapping("/details")
    public ModelAndView edit(@RequestParam Integer id,
                             Map<String, Object> map) {
         OriginalApply originalApply = originalApplyService.findByIdNotNull(id);
         String[] picList = originalApply.getContent().split(",");
        map.put("files",picList);
        return new ModelAndView("origin/details", map);
    }
}
