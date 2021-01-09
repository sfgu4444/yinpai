package com.yinpai.server.service;

import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.BaseFilterDto;
import com.yinpai.server.domain.entity.UserDeposit;
import com.yinpai.server.domain.repository.UserDepositRepository;
import com.yinpai.server.domain.repository.UserOrderRepository;
import com.yinpai.server.vo.PayDepositListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/21 6:49 下午
 */
@Service
public class UserDepositService {

    private final UserDepositRepository userDepositRepository;


    @Autowired
    public UserDepositService(UserDepositRepository userDepositRepository) {
        this.userDepositRepository = userDepositRepository;
    }

    public PageResponse<PayDepositListVo> list(Integer userId, BaseFilterDto baseFilterDto) {
        Page<UserDeposit> payRecordPage = userDepositRepository.findAllByUserId(userId, baseFilterDto.getSetPageable());
        List<PayDepositListVo> voList = new ArrayList<>();
        payRecordPage.forEach( r -> {
            PayDepositListVo vo = new PayDepositListVo();
            vo.setPayMoney(r.getPayMoney());
            vo.setUserMoney(r.getUserMoney());
            vo.setCreateTime(r.getCreateTime());
            voList.add(vo);
        });
        return PageResponse.of(voList, baseFilterDto.getPageable(), payRecordPage.getTotalElements());
    }

    public UserDeposit save(UserDeposit userPayRecord) {
        return userDepositRepository.save(userPayRecord);
    }

}
