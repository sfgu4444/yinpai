package com.yinpai.server.controller.admin;

import com.yinpai.server.domain.entity.User;
import com.yinpai.server.service.UserService;
import com.yinpai.server.utils.PageUtil;
import com.yinpai.server.utils.ResultUtil;
import com.yinpai.server.vo.admin.ResultVO;
import lombok.extern.slf4j.Slf4j;
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
 * @date 2020/9/28 6:01 下午
 */
@Slf4j
@Controller("adminUserController")
@RequestMapping("/admin/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size,
                             Map<String, Object> map) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest request = PageRequest.of(page - 1, size, sort);

        HashMap<String, String> conditionMap = new HashMap<>();
        conditionMap.put("userName", "like");
        Page<User> list = userService.findFilterAll(conditionMap, request);
        map.put("info", list);
        map.put("html", PageUtil.pageHtml(list.getTotalElements(), page, size));
        return new ModelAndView("user/list", map);
    }

    @PostMapping("/status")
    @ResponseBody
    public ResultVO changeAdminStatus(@RequestParam Integer id) {
        Integer adminStatus = userService.changeUserStatus(id);
        return ResultUtil.successData(adminStatus);
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultVO delete(@RequestParam Integer id) {
        userService.delete(id);
        return ResultUtil.success("用户删除成功");
    }
}
