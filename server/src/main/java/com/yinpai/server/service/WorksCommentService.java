package com.yinpai.server.service;

import com.yinpai.server.domain.dto.LoginAdminInfoDto;
import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.WorksCommentFilterDto;
import com.yinpai.server.domain.dto.fiter.WorksFilterDto;
import com.yinpai.server.domain.entity.User;
import com.yinpai.server.domain.entity.Works;
import com.yinpai.server.domain.entity.WorksComment;
import com.yinpai.server.domain.entity.admin.Admin;
import com.yinpai.server.domain.repository.WorksCommentRepository;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.thread.threadlocal.LoginAdminThreadLocal;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import com.yinpai.server.utils.ProjectUtil;
import com.yinpai.server.vo.WorksCommentListVo;
import com.yinpai.server.vo.admin.AdminWorksCommentListVo;
import com.yinpai.server.vo.admin.AdminWorksListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/30 9:24 下午
 */
@Service
public class WorksCommentService {

    private final WorksCommentRepository worksCommentRepository;

    private final UserService userService;

    private final AdminService adminService;

    private final WorksService worksService;

    @Autowired
    public WorksCommentService(WorksCommentRepository worksCommentRepository, @Lazy UserService userService, @Lazy AdminService adminService, @Lazy WorksService worksService) {
        this.worksCommentRepository = worksCommentRepository;
        this.userService = userService;
        this.adminService = adminService;
        this.worksService = worksService;
    }

    public Page<AdminWorksCommentListVo> findFilterAll(Map<String, String> map, Pageable pageable) {
        LoginAdminInfoDto adminInfoDto = LoginAdminThreadLocal.get();
        Page<WorksComment> works;
        if (adminInfoDto.isSuperAdmin()) {
            works = worksCommentRepository.findAll(ProjectUtil.getSpecification(map), pageable);
        } else {
            HttpServletRequest request = ProjectUtil.getRequest();
            WorksCommentFilterDto dto = new WorksCommentFilterDto();
            dto.setContent(request.getParameter("content"));
            dto.setWorkIds(worksService.adminWorkIds(adminInfoDto.getAdminId()));
            works = worksCommentRepository.findAll(getSpecification(dto), pageable);
        }

        List<AdminWorksCommentListVo> voList = new ArrayList<>();
        works.forEach(w -> {
            AdminWorksCommentListVo vo = new AdminWorksCommentListVo();
            vo.setId(w.getId());
            Works works1 = worksService.findById(w.getWorkId());
            if (works1 != null) {
                vo.setTitle(works1.getTitle());
                vo.setCoverImageUrl(works1.coverImageUrl);
            }
            User user = userService.findById(w.getUserId());
            if (user != null) {
                vo.setNickName(user.getNickName());
                vo.setAvatarUrl(user.getAvatarUrl());
            }
            vo.setContent(w.getContent());
            vo.setReplyContent(w.getReplyContent());
            vo.setCreateTime(w.getCreateTime());
            vo.setReplyTime(w.getReplyTime());
            voList.add(vo);
        });
        return new PageImpl<>(voList, works.getPageable(), works.getTotalElements());
    }

    public WorksComment findByIdNotNull(Integer commentId) {
        return worksCommentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("评论记录不存在"));
    }

    private Specification<WorksComment> getSpecification(WorksCommentFilterDto filterDto) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicatesList = new ArrayList<>();
            Optional.ofNullable(filterDto.getWorkId()).ifPresent(u -> predicatesList.add(criteriaBuilder.equal(root.get("workId"), u)));
            Optional.ofNullable(filterDto.getWorkIds()).ifPresent(u -> predicatesList.add(root.get("workId").in(u)));
            Predicate[] predicates = new Predicate[predicatesList.size()];
            return criteriaBuilder.and(predicatesList.toArray(predicates));
        };
    }

    public PageResponse<WorksCommentListVo> commentList(WorksCommentFilterDto filterDto) {
        Page<WorksComment> worksPage = worksCommentRepository.findAll(getSpecification(filterDto), filterDto.getPageable());
        List<WorksCommentListVo> voList = new ArrayList<>();
        worksPage.forEach(w -> {
            WorksCommentListVo vo = new WorksCommentListVo();
            vo.setUserId(w.getUserId());
            User user = userService.findById(w.getUserId());
            if (user != null) {
                vo.setNickName(user.getNickName());
                vo.setAvatarUrl(user.getAvatarUrl());
            }
            vo.setContent(w.getContent());
            vo.setAdminId(w.getAdminId());
            Admin admin = adminService.findById(w.getAdminId());
            if (admin != null) {
                vo.setAdminNickName(admin.getNickName());
                vo.setAdminAvatarUrl(admin.getAvatarUrl());
            }
            vo.setReplyContent(w.getReplyContent());
            vo.setCreateTime(w.getCreateTime());
            vo.setReplyTime(w.getReplyTime());
            voList.add(vo);
        });
        return PageResponse.of(voList, filterDto.getPageable(), worksPage.getTotalElements());
    }

    public long commentCount(Integer workId) {
        return worksCommentRepository.countAllByWorkId(workId);
    }

    public Integer addComment(Integer workId, String content) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        Works works = worksService.findByIdNotNull(workId);
        WorksComment worksComment = new WorksComment();
        worksComment.setWorkId(workId);
        worksComment.setUserId(userInfoDto.getUserId());
        worksComment.setContent(content);
        worksComment.setAdminId(works.getAdminId());
        worksComment.setCreateTime(new Date());
        WorksComment result = worksCommentRepository.save(worksComment);
        return result.getId();
    }

    public void replyComment(Integer id, String replyContent) {
        WorksComment worksComment = findByIdNotNull(id);
        worksComment.setReplyContent(replyContent);
        worksComment.setReplyTime(new Date());
        worksCommentRepository.save(worksComment);
    }

    public void delete(Integer id) {
        WorksComment w = findByIdNotNull(id);
        worksCommentRepository.delete(w);
    }
}
