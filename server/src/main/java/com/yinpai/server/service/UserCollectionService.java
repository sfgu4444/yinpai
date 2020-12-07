package com.yinpai.server.service;

import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.UserCollectionFilterDto;
import com.yinpai.server.domain.entity.UserCollection;
import com.yinpai.server.domain.entity.Works;
import com.yinpai.server.domain.entity.admin.Admin;
import com.yinpai.server.domain.repository.UserCollectionRepository;
import com.yinpai.server.domain.repository.WorksRepository;
import com.yinpai.server.enums.CommonEnum;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import com.yinpai.server.vo.IndexWorksVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/5 10:03 下午
 */
@Service
public class UserCollectionService {

    private final UserCollectionRepository userCollectionRepository;

    private final WorksRepository worksRepository;

    private final AdminService adminService;

    private final UserFollowService userFollowService;
    @Autowired
    public UserCollectionService(UserCollectionRepository userCollectionRepository, WorksRepository worksRepository, @Lazy AdminService adminService, @Lazy UserFollowService userFollowService) {
        this.userCollectionRepository = userCollectionRepository;
        this.worksRepository = worksRepository;
        this.adminService = adminService;
        this.userFollowService = userFollowService;
    }

    public Integer collectionWork(Integer workId) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请重新登陆");
        }
        UserCollection userCollection = userCollectionRepository.findByUserIdAndWorkId(userInfoDto.getUserId(), workId);
        if (userCollection == null) {
            UserCollection save = new UserCollection();
            save.setWorkId(workId);
            save.setUserId(userInfoDto.getUserId());
            save.setCreateTime(new Date());
            save.setUpdateTime(new Date());
            userCollectionRepository.save(save);
            return CommonEnum.YES.getCode();
        }
        userCollectionRepository.delete(userCollection);
        return CommonEnum.NO.getCode();
    }

    public Long userCollectionCount(Integer userId) {
        return userCollectionRepository.countAllByUserId(userId);
    }

    public Long workCollectionCount(Integer workId) {
        return userCollectionRepository.countAllByWorkId(workId);
    }

    private Specification<UserCollection> getSpecification(UserCollectionFilterDto filterDto) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicatesList = new ArrayList<>();
            Optional.ofNullable(filterDto.getUserId()).ifPresent(u -> predicatesList.add(criteriaBuilder.equal(root.get("userId"), u)));
            Join<UserCollection, Works> workJoin = root.join("works", JoinType.INNER);
            Optional.ofNullable(filterDto.getType()).ifPresent( u -> predicatesList.add(criteriaBuilder.equal(workJoin.get("type"), u)));
            Predicate[] predicates = new Predicate[predicatesList.size()];
            return criteriaBuilder.and(predicatesList.toArray(predicates));
        };
    }

    public PageResponse<IndexWorksVo> userCollectionList(UserCollectionFilterDto filterDto) {
        filterDto.setStatus(CommonEnum.YES.getCode());
        Page<UserCollection> userCollection = userCollectionRepository.findAll(getSpecification(filterDto), filterDto.getPageable());
        List<IndexWorksVo> voList = new ArrayList<>();
        userCollection.forEach(w -> {
            IndexWorksVo vo = new IndexWorksVo();
            if (w.getWorks() != null) {
                vo.setId(w.getWorks().getId());
                vo.setTitle(w.getWorks().getTitle());
                vo.setContent(w.getWorks().getContent());
                vo.setCoverImageUrl(w.getWorks().getCoverImageUrl());
                vo.setType(w.getWorks().getType());
                vo.setAdminId(w.getWorks().getAdminId());
                Admin admin = adminService.findById(w.getWorks().getAdminId());
                vo.setNickName(admin.getNickName());
                vo.setAvatarUrl(admin.getAvatarUrl());
                vo.setCreateTime(w.getCreateTime());
                vo.setLookCount(w.getWorks().getLookCount());
                LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
                if (userInfoDto != null) {
                    vo.setFollow(userFollowService.isFollow(userInfoDto.getUserId(), w.getWorks().getAdminId()));
                }
            }
            voList.add(vo);
        });
        return PageResponse.of(voList, filterDto.getPageable(), userCollection.getTotalElements());
    }

    public boolean isCollection(Integer userId, Integer workId) {
        UserCollection userCollection = userCollectionRepository.findByUserIdAndWorkId(userId, workId);
        return userCollection != null;
    }
}
