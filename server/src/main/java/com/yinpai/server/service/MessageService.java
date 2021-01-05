package com.yinpai.server.service;

import com.yinpai.server.domain.dto.LoginAdminInfoDto;
import com.yinpai.server.domain.entity.admin.Admin;
import com.yinpai.server.domain.entity.admin.Message;
import com.yinpai.server.domain.repository.admin.AdminRepository;
import com.yinpai.server.domain.repository.admin.MessageRepository;
import com.yinpai.server.exception.ProjectException;
import com.yinpai.server.thread.threadlocal.LoginAdminThreadLocal;
import com.yinpai.server.utils.ProjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @program server
 * @description:
 * @author: liuzhenda
 * @create: 2021/01/04 22:15
 */
@Service
@Slf4j
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;

    private final UserService userService;

    private final AdminService adminService;

    private final AdminRepository adminRepository;


    @Autowired
    public MessageService(MessageRepository messageRepository, UserService userService, AdminService adminService, AdminRepository adminRepository) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.adminService = adminService;
        this.adminRepository = adminRepository;
    }

    public Page<Message> findFilterAll(Pageable pageable) {
        LoginAdminInfoDto adminInfoDto = LoginAdminThreadLocal.get();
        HashMap<String, String> conditionMap = new HashMap<>();
        conditionMap.put("message", "like");
        Page<Message> recordRepositoryAll;
        if (adminInfoDto.isSuperAdmin()) {
            recordRepositoryAll = messageRepository.findAll(ProjectUtil.getSpecification(conditionMap), pageable);
        } else {
            recordRepositoryAll = messageRepository.findAllByReceive(adminInfoDto.getAdminId(), pageable);
        }

        for (Message message : recordRepositoryAll) {
            Admin send = adminService.findById(message.getSend());
            Admin receive = adminService.findById(message.getReceive());
            if (null != send) {
                message.setSendName(send.getNickName());
            }
            if (null != receive) {
                message.setReceiveName(receive.getNickName());
            }
        }
        return recordRepositoryAll;
    }

    public void delete(Integer id) {
        Message message = findByIdNotNull(id);
        messageRepository.delete(message);
    }

    public Message findByIdNotNull(Integer id) {
        return messageRepository.findById(id).orElseThrow(() -> new ProjectException("消息"));
    }

    public Message add(Message message) {
        try {
            LoginAdminInfoDto adminInfoDto = LoginAdminThreadLocal.get();
            message.setSend(adminInfoDto.getAdminId());
            Admin admin = adminService.findById(adminInfoDto.getAdminId());
            message.setSendName(admin.getAdminName());
            Admin receiveAdmin = adminRepository.findByAdminName(message.getReceiveName());
            message.setReceiveName(receiveAdmin.getAdminName());
            message.setReceive(receiveAdmin.getId());
            message.setIsRead(0);
            return messageRepository.save(message);
        } catch (Exception e) {
            throw new ProjectException("消息发送失败");
        }
    }

    public Integer IsReadMessage() {
        Integer messageTotal = 0;
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest request = PageRequest.of(0, 10000, sort);
        LoginAdminInfoDto adminInfoDto = LoginAdminThreadLocal.get();
        Page<Message> recordRepositoryAll = messageRepository.findAllByReceive(adminInfoDto.getAdminId(), request);
        if (null == recordRepositoryAll.getContent() || recordRepositoryAll.getContent().size() == 0) {
            return messageTotal;
        }

        for (Message message : recordRepositoryAll) {
            if (message.getIsRead() == 0) {
                messageTotal++;
            }
        }
        log.info("【获取当前登陆人未读消息】: {} , {}", messageTotal, adminInfoDto.getAdminId());
        return messageTotal;
    }

    public List<Admin> adminList() {
        List<Admin> adminList = new ArrayList<>();
        List<Admin> list = adminRepository.findAll();
        for (Admin admin : list) {
            if (LoginAdminThreadLocal.get().getAdminId().equals(admin.getId())) {
                continue;
            }
            adminList.add(admin);
        }
        return adminList;
    }

    public void edit(Message message) {
        if (LoginAdminThreadLocal.get().getAdminId().equals(message.getReceive())) {
            message.setIsRead(1);
            messageRepository.save(message);
        }
    }
}
