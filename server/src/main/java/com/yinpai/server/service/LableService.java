package com.yinpai.server.service;

import com.yinpai.server.domain.entity.Lable;
import com.yinpai.server.domain.entity.admin.Message;
import com.yinpai.server.domain.repository.LableRepository;
import com.yinpai.server.exception.ProjectException;
import com.yinpai.server.utils.ProjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

/**
 * @program server
 * @description: 标签管理
 * @author: liuzhenda
 * @create: 2021/01/05 16:41
 */
@Service
@Slf4j
@Transactional
public class LableService {

    private final LableRepository lableRepository;

    public LableService(LableRepository lableRepository) {
        this.lableRepository = lableRepository;
    }

    public Page<Message> findAll(Pageable pageable) {
        HashMap<String, String> conditionMap = new HashMap<>();
        conditionMap.put("lableName", "like");
        return lableRepository.findAll(ProjectUtil.getSpecification(conditionMap), pageable);
    }

    public void add(Lable lable) {
        Lable lable1 = lableRepository.findByLableName(lable.getLableName());
        if (null != lable1) {
            throw new ProjectException("标签重复");
        }
        lableRepository.save(lable);
    }

    public Lable findByIdNotNull(Integer id) {
        return lableRepository.findById(id).orElseThrow(() -> new ProjectException("标签为空"));
    }

    public Lable findById(Integer id) {
        return lableRepository.findById(id).orElse(new Lable());
    }

    public void edit(Lable lable) {
        if(StringUtils.isBlank(lable.getId()+"")||StringUtils.isBlank(lable.getLableName())){
            throw new ProjectException("参数为空");
        }
        Lable lables = findByIdNotNull(lable.getId());
        Lable lable1 = lableRepository.findByLableName(lable.getLableName());
        if(null != lable1 && !lable1.getId().equals(lables)){
            throw new ProjectException("标签重复");
        }
        lableRepository.save(lable);
    }

    public List<Lable> findAll(){
      return lableRepository.findAll();
    }


    public void delete(Integer id) {
        Lable lable = findByIdNotNull(id);
        lableRepository.delete(lable);
    }

}
