package com.yinpai.server.service;

import java.util.*;

import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.entity.User;
import com.yinpai.server.domain.entity.UserAdvice;
import com.yinpai.server.domain.repository.UserAdviceRepository;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.exception.ProjectException;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import com.yinpai.server.utils.ProjectUtil;
import com.yinpai.server.vo.admin.Advice;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/6 12:13 上午
 */
@Service
@Transactional
@Slf4j
public class UserAdviceService {

    private final UserAdviceRepository userAdviceRepository;

    private final UserService userService;

    @Autowired
    public UserAdviceService(UserAdviceRepository userAdviceRepository, UserService userService) {
        this.userAdviceRepository = userAdviceRepository;
        this.userService = userService;
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

    public Page<Advice> findFilterAll(Pageable pageable) {
        HttpServletRequest request = ProjectUtil.getRequest();
        String nickName = request.getParameter("nickName");
        log.info("【意见反馈 检索条件】: {}",nickName);
        Page<UserAdvice> pageAdvice = null;
        List<Advice> voList = new ArrayList<>();
        if(StringUtils.isBlank(nickName)){
            pageAdvice = userAdviceRepository.findAll(ProjectUtil.getSpecification(new HashMap<>()), pageable);
        }else {
           List<Integer> userIds = userService.findByNicknameListId(nickName);
           pageAdvice = userAdviceRepository.findByUserIdIn(userIds,pageable);
        }
        if(null == pageable){
            return new PageImpl<>(voList, pageAdvice.getPageable(), pageAdvice.getTotalElements());
        }
        pageAdvice.forEach(w -> {
            Advice vo = new Advice();
            vo.setId(w.getId());
            vo.setEmail(w.getEmail());
            User user = userService.findById(w.getUserId());
            if (user != null) {
                vo.setUser(user);
            }
            vo.setContent(w.getContent());
            vo.setCreateTime(w.getCreateTime());
            voList.add(vo);
        });
        return new PageImpl<>(voList, pageAdvice.getPageable(), pageAdvice.getTotalElements());
    }

    public void delete(Integer id) {
        UserAdvice userAdvice = findByIdNotNull(id);
        userAdviceRepository.delete(userAdvice);
    }

    public UserAdvice findByIdNotNull(Integer id) {
        return userAdviceRepository.findById(id).orElseThrow(() -> new ProjectException("建议不存在"));
    }

}
