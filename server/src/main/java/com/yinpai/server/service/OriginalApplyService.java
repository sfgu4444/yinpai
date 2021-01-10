package com.yinpai.server.service;
import java.util.Date;
import java.util.Map;

import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.entity.OriginalApply;
import com.yinpai.server.domain.entity.User;
import com.yinpai.server.domain.entity.UserAdvice;
import com.yinpai.server.domain.repository.OriginalApplyRepository;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.exception.ProjectException;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import com.yinpai.server.utils.ProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/6 12:39 上午
 */
@Service
public class OriginalApplyService {

    private final OriginalApplyRepository originalApplyRepository;

    @Autowired
    public OriginalApplyService(OriginalApplyRepository originalApplyRepository) {
        this.originalApplyRepository = originalApplyRepository;
    }


    public void originalApply(String contactWay, String qq, String wx, String email, String content) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        OriginalApply originalApply = new OriginalApply();
        originalApply.setContactWay(contactWay);
        originalApply.setWx(wx);
        originalApply.setQq(qq);
        originalApply.setEmail(email);
        originalApply.setContent(content);
        originalApply.setCreateTime(new Date());
        originalApplyRepository.save(originalApply);
    }

    public Page<OriginalApply> findFilterAll(Map<String, String> map, Pageable pageable) {
        return originalApplyRepository.findAll(ProjectUtil.getSpecification(map), pageable);
    }

    public void delete(Integer id) {
        OriginalApply originalApply = findByIdNotNull(id);
        originalApplyRepository.delete(originalApply);
    }

    public OriginalApply findByIdNotNull(Integer id) {
        return originalApplyRepository.findById(id).orElseThrow(() -> new ProjectException("原创不存在"));
    }

}
