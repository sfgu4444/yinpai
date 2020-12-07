package com.yinpai.server.controller.admin;

import com.yinpai.server.service.WorksCommentService;
import com.yinpai.server.utils.PageUtil;
import com.yinpai.server.utils.ResultUtil;
import com.yinpai.server.vo.admin.AdminWorksCommentListVo;
import com.yinpai.server.vo.admin.AdminWorksListVo;
import com.yinpai.server.vo.admin.ResultVO;
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
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/6 4:51 下午
 */
@Controller("adminWorksCommentController")
@RequestMapping("/admin/workComment")
public class WorksCommentController {

    private final WorksCommentService worksCommentService;

    @Autowired
    public WorksCommentController(WorksCommentService worksCommentService) {
        this.worksCommentService = worksCommentService;
    }

    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size,
                             Map<String, Object> map) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest request = PageRequest.of(page - 1, size, sort);
        HashMap<String, String> conditionMap = new HashMap<>();
        conditionMap.put("content", "like");
        Page<AdminWorksCommentListVo> list = worksCommentService.findFilterAll(conditionMap, request);
        map.put("info", list);
        map.put("html", PageUtil.pageHtml(list.getTotalElements(), page, size));
        return new ModelAndView("workComment/list", map);
    }

    @GetMapping("/reply")
    public ModelAndView reply(@RequestParam Integer id, Map<String, Object> map) {
        map.put("id", id);
        return new ModelAndView("workComment/reply",map);
    }

    @PostMapping("/reply")
    @ResponseBody
    public ResultVO replyData(@RequestParam Integer id,
                              @RequestParam String replyContent) {
        worksCommentService.replyComment(id, replyContent);
        return ResultUtil.success("回复成功");
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultVO delete(@RequestParam Integer id) {
        worksCommentService.delete(id);
        return ResultUtil.success("评论删除成功");
    }

}
