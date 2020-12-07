package com.yinpai.server.service;

import java.util.Date;

import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.entity.UserAdvice;
import com.yinpai.server.domain.repository.UserAdviceRepository;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/6 12:13 上午
 */
@Service
public class UserAdviceService {

    private final UserAdviceRepository userAdviceRepository;

    @Autowired
    public UserAdviceService(UserAdviceRepository userAdviceRepository) {
        this.userAdviceRepository = userAdviceRepository;
    }

    public void addAdvice(String content, String email) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        UserAdvice userAdvice = new UserAdvice();
        userAdvice.setUserId(userInfoDto.getUserId());
        userAdvice.setContent(content);
        userAdvice.setEmail(email);
        userAdvice.setCreateTime(new Date());
        userAdvice.setUpdateTime(new Date());
        userAdviceRepository.save(userAdvice);
    }
}
