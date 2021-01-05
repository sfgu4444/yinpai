package com.yinpai.server.controller.admin;

import com.yinpai.server.service.MessageService;
import com.yinpai.server.thread.threadlocal.LoginAdminThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/")
public class IndexController {

    private final MessageService  messageService;

    public IndexController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/index")
    public ModelAndView index(Map<String, Object> map){
        map.put("superAdmin", LoginAdminThreadLocal.get().isSuperAdmin());
        map.put("messageTotal",messageService.IsReadMessage());
        return new ModelAndView("index/index", map);
    }

    @GetMapping("/index/welcome")
    public ModelAndView welcome(){
        return new ModelAndView("index/welcome");
    }
}
