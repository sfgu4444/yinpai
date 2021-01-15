package com.yinpai.server.service;
import java.util.Date;

import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.BaseFilterDto;
import com.yinpai.server.domain.dto.fiter.UserFollowFilterDto;
import com.yinpai.server.domain.dto.fiter.WorksFilterDto;
import com.yinpai.server.domain.entity.UserFollow;
import com.yinpai.server.domain.entity.admin.Admin;
import com.yinpai.server.domain.repository.UserFollowRepository;
import com.yinpai.server.vo.IndexWorksVo;
import com.yinpai.server.vo.UserFollowListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 2:43 下午
 */
@Slf4j
@Service
public class UserFollowService {

    private final UserFollowRepository userFollowRepository;

    private final AdminService adminService;

    private final WorksService worksService;

    @Autowired
    public UserFollowService(UserFollowRepository userFollowRepository, @Lazy AdminService adminService,@Lazy WorksService worksService) {
        this.userFollowRepository = userFollowRepository;
        this.adminService = adminService;
        this.worksService = worksService;
    }

    public boolean isFollow(Integer userId, Integer adminId) {
        return userFollowRepository.findByUserIdAndAdminId(userId, adminId) != null;
    }

    public long adminFansNumber(Integer adminId) {
        return userFollowRepository.countAllByAdminId(adminId);
    }

    public Long userFollowCount(Integer userId) {
        return userFollowRepository.countAllByUserId(userId);
    }

    private Specification<UserFollow> getSpecification(UserFollowFilterDto filterDto) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicatesList = new ArrayList<>();
            Optional.ofNullable(filterDto.getUserId()).ifPresent(u -> predicatesList.add(criteriaBuilder.equal(root.get("userId"), u)));
            Predicate[] predicates = new Predicate[predicatesList.size()];
            return criteriaBuilder.and(predicatesList.toArray(predicates));
        };
    }

    public PageResponse<UserFollowListVo> userFollowList(UserFollowFilterDto followFilterDto) {
        Page<UserFollow> userFollowPage = userFollowRepository.findAll(getSpecification(followFilterDto), followFilterDto.getPageable());
        List<UserFollowListVo> voList = new ArrayList<>();
        userFollowPage.forEach( f -> {
            UserFollowListVo vo = new UserFollowListVo();
            log.info("【获取关注商家】 userid : {} , adminid : {}",followFilterDto.getUserId(),f.getAdminId());
            Admin admin = adminService.findByIdNotNull(f.getAdminId());
            if (admin != null) {
                vo.setAdminId(admin.getId());
                vo.setNickName(admin.getNickName());
                vo.setAvatarUrl(admin.getAvatarUrl());
                vo.setUpdateCount(worksService.getAdminUpdateCount(admin.getId()));
            }
            voList.add(vo);
        });
        return PageResponse.of(voList, followFilterDto.getPageable(), userFollowPage.getTotalElements());
    }

    public Integer userFollowAdmin(Integer adminId, Integer userId) {
        UserFollow userFollow = userFollowRepository.findByUserIdAndAdminId(userId, adminId);
        if (userFollow == null) {
            userFollow = new UserFollow();
            userFollow.setUserId(userId);
            userFollow.setAdminId(adminId);
            userFollow.setCreateTime(new Date());
            userFollow.setUpdateTime(new Date());
            userFollowRepository.save(userFollow);
            return 1;
        } else {
            userFollowRepository.delete(userFollow);
            return 0;
        }
    }

    public PageResponse<IndexWorksVo> followWorksList(Integer userId, WorksFilterDto worksFilterDto) {
        List<UserFollow> userFollowList = userFollowRepository.findAllByUserId(userId);
        if (userFollowList.size() < 1) {
            return PageResponse.EMPTY;
        }
        worksFilterDto.setAdminIds(userFollowList.stream().map(UserFollow::getAdminId).collect(Collectors.toList()));
        return worksService.index(worksFilterDto);
    }
}
