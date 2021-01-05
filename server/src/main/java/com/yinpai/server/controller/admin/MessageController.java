package com.yinpai.server.controller.admin;

import com.yinpai.server.domain.entity.admin.Message;
import com.yinpai.server.domain.repository.admin.AdminRepository;
import com.yinpai.server.service.MessageService;
import com.yinpai.server.thread.threadlocal.LoginAdminThreadLocal;
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

@Slf4j
@Controller
@RequestMapping("/admin/message")
public class MessageController {

    private final MessageService messageService;

    private final AdminRepository adminRepository;

    @Autowired
    public MessageController(MessageService messageService, AdminRepository adminRepository) {
        this.messageService = messageService;
        this.adminRepository = adminRepository;
    }

    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size,
                             Map<String, Object> map) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest request = PageRequest.of(page - 1, size, sort);
        Page<Message> list = messageService.findFilterAll(request);
        map.put("info", list);
        map.put("html", PageUtil.pageHtml(list.getTotalElements(), page, size));
        map.put("superAdmin", LoginAdminThreadLocal.get().isSuperAdmin());
        return new ModelAndView("message/list", map);
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultVO delete(@RequestParam Integer id) {
        messageService.delete(id);
        return ResultUtil.success("消息删除成功");
    }

    @GetMapping("/add")
    public ModelAndView add() {
        Map<String, Object> map = new HashMap<>();
        map.put("info", messageService.adminList());
        return new ModelAndView("message/add",map);
    }


    @PostMapping("/add")
    @ResponseBody
    public ResultVO add(Message message) {
        messageService.add(message);
        return ResultUtil.success("消息发送成功");
    }

    @GetMapping("/edit")
    public ModelAndView edit(@RequestParam Integer id) {
        Map<String, Object> map = new HashMap<>();
        Message message = messageService.findByIdNotNull(id);
        messageService.edit(message);
        map.put("data", message);
        return new ModelAndView("message/edit", map);
    }
}
