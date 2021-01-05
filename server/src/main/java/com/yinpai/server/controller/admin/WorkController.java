package com.yinpai.server.controller.admin;

import com.yinpai.server.domain.entity.Lable;
import com.yinpai.server.domain.entity.Works;
import com.yinpai.server.service.LableService;
import com.yinpai.server.service.WorksResourcesService;
import com.yinpai.server.service.WorksService;
import com.yinpai.server.utils.PageUtil;
import com.yinpai.server.utils.ProjectUtil;
import com.yinpai.server.utils.ResultUtil;
import com.yinpai.server.vo.admin.AdminWorksListVo;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 6:17 下午
 */
@Slf4j
@Controller("adminWorkController")
@RequestMapping("/admin/work")
public class WorkController {

    private final WorksService worksService;

    private final WorksResourcesService worksResourcesService;

    private final LableService lableService;

    @Autowired
    public WorkController(WorksService worksService, WorksResourcesService worksResourcesService, LableService lableService) {
        this.worksService = worksService;
        this.worksResourcesService = worksResourcesService;
        this.lableService = lableService;
    }

    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size,
                             Map<String, Object> map) {
        HttpServletRequest servletRequest = ProjectUtil.getRequest();
        log.info("【作品排序方式】: {}",servletRequest.getParameter("sort"));
         String sort1 = servletRequest.getParameter("sort");
        String name = "";
        if(StringUtils.isBlank(sort1)){
            sort1 = "id";
        }else {
            if("0".equals(sort1)){
                sort1 = "collectionsCount";
                name = "收藏数";
            }else if("1".equals(sort1)){
                sort1 = "lookCount";
                name = "浏览量";
            }else if("2".equals(sort1)){
                sort1 = "downloadCount";
                name = "下载量";
            }
        }
        Sort sort = Sort.by(Sort.Direction.DESC, sort1);
        PageRequest request = PageRequest.of(page - 1, size, sort);
        HashMap<String, String> conditionMap = new HashMap<>();
        conditionMap.put("title", "like");
        log.info("【title 查询】: {}",servletRequest.getParameter("title"));

        Page<AdminWorksListVo> list = worksService.findFilterAll(conditionMap, request);
        map.put("title", servletRequest.getParameter("title"));
        map.put("name", name);
        map.put("info", list);
        map.put("html", PageUtil.pageHtml(list.getTotalElements(), page, size));
        return new ModelAndView("work/list", map);
    }

    @GetMapping("/add")
    public ModelAndView add(Map<String, Object> map) {
        map.put("folder", System.currentTimeMillis());
        List<Lable> lableList = lableService.findAll();
        map.put("lableList", lableList);
        return new ModelAndView("work/add", map);
    }

    @PostMapping("/add")
    @ResponseBody
    public ResultVO addWork(@RequestBody SaveWorkVo vo) throws IOException {
        worksService.addWork(vo);
        return ResultUtil.success();
    }

    @GetMapping("/edit")
    public ModelAndView edit(@RequestParam Integer id,
                             Map<String, Object> map) {
        Works works = worksService.findByIdNotNull(id);
        map.put("data", works);
        map.put("folder", System.currentTimeMillis());
        List<String> workResource = worksResourcesService.getWorkResource(id);
        if (works.getType() == 1) {
            map.put("files", workResource);
        } else {
            map.put("file", workResource.get(0));
        }
        List<Lable> lableList = lableService.findAll();
        map.put("lableList", lableList);
        return new ModelAndView("work/edit", map);
    }

    @PostMapping("/edit")
    @ResponseBody
    public ResultVO editData(@RequestBody SaveWorkVo vo) {
        worksService.editWork(vo);
        return ResultUtil.success();
    }

    @PostMapping("/status")
    @ResponseBody
    public ResultVO changeStatus(@RequestParam Integer id) {
        Integer status = worksService.changeStatus(id);
        return ResultUtil.successData(status);
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultVO delete(@RequestParam Integer id) {
        worksService.delete(id);
        return ResultUtil.success("作品删除成功");
    }

}
