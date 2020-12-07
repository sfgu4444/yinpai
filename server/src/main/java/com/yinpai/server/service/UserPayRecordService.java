package com.yinpai.server.service;

import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.BaseFilterDto;
import com.yinpai.server.domain.entity.UserPayRecord;
import com.yinpai.server.domain.entity.admin.Admin;
import com.yinpai.server.domain.repository.UserPayRecordRepository;
import com.yinpai.server.vo.PayRecordListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/21 6:27 下午
 */
@Service
public class UserPayRecordService {

    private final UserPayRecordRepository userPayRecordRepository;

    private final AdminService adminService;

    private final WorksService worksService;

    @Autowired
    public UserPayRecordService(UserPayRecordRepository userPayRecordRepository, @Lazy AdminService adminService, @Lazy WorksService worksService) {
        this.userPayRecordRepository = userPayRecordRepository;
        this.adminService = adminService;
        this.worksService = worksService;
    }


    public PageResponse<PayRecordListVo> list(Integer userId, BaseFilterDto baseFilterDto) {
        Page<UserPayRecord> payRecordPage = userPayRecordRepository.findAllByUserId(userId, baseFilterDto.getSetPageable());
        List<PayRecordListVo> voList = new ArrayList<>();
        payRecordPage.forEach( r -> {
            PayRecordListVo vo = new PayRecordListVo();
            Admin admin = adminService.findById(r.getAdminId());
            if (admin != null) {
                vo.setNickName(admin.getNickName());
            }
            vo.setMoney(r.getMoney());
            vo.setCreateTime(r.getCreateTime());
            voList.add(vo);
        });
        return PageResponse.of(voList, baseFilterDto.getPageable(), payRecordPage.getTotalElements());
    }

    public UserPayRecord save(UserPayRecord userPayRecord) {
        return userPayRecordRepository.save(userPayRecord);
    }
}
