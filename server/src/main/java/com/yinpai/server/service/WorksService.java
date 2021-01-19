package com.yinpai.server.service;

import com.yinpai.server.domain.dto.LoginAdminInfoDto;
import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.WorksFilterDto;
import com.yinpai.server.domain.entity.Lable;
import com.yinpai.server.domain.entity.UserDownloadRecord;
import com.yinpai.server.domain.entity.Works;
import com.yinpai.server.domain.entity.WorksLookLog;
import com.yinpai.server.domain.entity.admin.Admin;
import com.yinpai.server.domain.repository.LableRepository;
import com.yinpai.server.domain.repository.WorksLookLogRepository;
import com.yinpai.server.domain.repository.WorksRepository;
import com.yinpai.server.enums.CommonEnum;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.exception.ProjectException;
import com.yinpai.server.thread.threadlocal.LoginAdminThreadLocal;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import com.yinpai.server.utils.ProjectUtil;
import com.yinpai.server.utils.sensitive.SensitiveFilterService;
import com.yinpai.server.vo.IndexWorksVo;
import com.yinpai.server.vo.WorkDetailVo;
import com.yinpai.server.vo.admin.AdminWorksListVo;
import com.yinpai.server.vo.admin.SaveWorkVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 2:19 下午
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WorksService {

    private final WorksRepository worksRepository;

    private final UserService userService;

    private final UserFollowService userFollowService;

    private final WorksResourcesService worksResourcesService;

    private final UserCollectionService userCollectionService;

    private final AdminService adminService;

    private final UserPayService userPayService;

    private final UserDownloadRecordService userDownloadRecordService;

    private final WorksCommentService worksCommentService;

    private final WorksLookLogRepository worksLookLogRepository;

    private final LableRepository lableRepository;

    private final LableService lableService;

    @Autowired
    public WorksService(WorksRepository worksRepository, UserService userService, UserFollowService userFollowService, WorksResourcesService worksResourcesService, @Lazy UserCollectionService userCollectionService, AdminService adminService, @Lazy UserPayService userPayService, @Lazy UserDownloadRecordService userDownloadRecordService, @Lazy WorksCommentService worksCommentService, WorksLookLogRepository worksLookLogRepository, LableRepository lableRepository, LableService lableService) {
        this.worksRepository = worksRepository;
        this.userService = userService;
        this.userFollowService = userFollowService;
        this.worksResourcesService = worksResourcesService;
        this.userCollectionService = userCollectionService;
        this.adminService = adminService;
        this.userPayService = userPayService;
        this.userDownloadRecordService = userDownloadRecordService;
        this.worksCommentService = worksCommentService;
        this.worksLookLogRepository = worksLookLogRepository;
        this.lableRepository = lableRepository;
        this.lableService = lableService;
    }

    public Set<Integer> adminWorkIds(Integer adminId) {
        return worksRepository.findAllByAdminId(adminId).stream().map(Works::getId).collect(Collectors.toSet());
    }

    public Page<AdminWorksListVo> findFilterAll(Map<String, String> map, Pageable pageable) {
        LoginAdminInfoDto adminInfoDto = LoginAdminThreadLocal.get();
        Page<Works> works;
        if (adminInfoDto.isSuperAdmin()) {
            works = worksRepository.findAll(ProjectUtil.getSpecification(map), pageable);
        } else {
            HttpServletRequest request = ProjectUtil.getRequest();
            WorksFilterDto dto = new WorksFilterDto();
            dto.setTitle(request.getParameter("title"));
            dto.setAdminId(adminInfoDto.getAdminId());
            works = worksRepository.findAll(getSpecification(dto), pageable);
        }
        List<AdminWorksListVo> voList = new ArrayList<>();
        works.forEach(w -> {
            AdminWorksListVo vo = new AdminWorksListVo();
            BeanUtils.copyProperties(w, vo);
            Admin admin = adminService.findById(w.getAdminId());
            if (admin != null) {
                vo.setAdminName(admin.getAdminName());
                vo.setNickName(admin.getNickName());
            }
            vo.setDownloadCount(userDownloadRecordService.workDownloadCount(w.getId()));
            vo.setCollectionCount(userCollectionService.workCollectionCount(w.getId()));
            if(null != w.getLable()){
                Lable lable = lableService.findByIdNotNull(w.getLable());
                if(null != lable){
                    vo.setLable(lable.getLableName());
                }
            }

            w.setCollectionsCount(vo.getCollectionCount());
            w.setDownloadCount(vo.getDownloadCount());
            worksRepository.save(w);
            voList.add(vo);
        });
        return new PageImpl<>(voList, works.getPageable(), works.getTotalElements());
    }

    private Specification<Works> getSpecification(WorksFilterDto filterDto) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicatesList = new ArrayList<>();
            Optional.ofNullable(filterDto.getType()).ifPresent(u -> predicatesList.add(criteriaBuilder.equal(root.get("type"), u)));
            Optional.ofNullable(filterDto.getAdminId()).ifPresent(u -> predicatesList.add(criteriaBuilder.equal(root.get("adminId"), u)));
            Optional.ofNullable(filterDto.getKeyword()).ifPresent(k -> {
                if (isNumeric(k)) {
                    predicatesList.add(criteriaBuilder.equal(root.get("adminId"), k));
                } else {
                    predicatesList.add(criteriaBuilder.or(criteriaBuilder.like(root.get("title"), "%" + k + "%"), criteriaBuilder.like(root.get("content"), "%" + k + "%")));
                }
            });
            Optional.ofNullable(filterDto.getTitle()).ifPresent(t -> {
                predicatesList.add(criteriaBuilder.like(root.get("title"), "%" + t + "%"));
            });
            Optional.ofNullable(filterDto.getAdminIds()).ifPresent(ids -> {
                predicatesList.add(root.get("adminId").in(ids));
            });
            Predicate[] predicates = new Predicate[predicatesList.size()];
            return criteriaBuilder.and(predicatesList.toArray(predicates));
        };
    }

    private boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public PageResponse<IndexWorksVo> index(WorksFilterDto filterDto) {
        filterDto.setStatus(CommonEnum.YES.getCode());
        Page<Works> worksPage = worksRepository.findAll(getSpecification(filterDto), filterDto.getPageable());
        List<IndexWorksVo> voList = new ArrayList<>();
        worksPage.forEach(w -> {
            IndexWorksVo vo = new IndexWorksVo();
            vo.setId(w.getId());
            vo.setTitle(w.getTitle());
            vo.setContent(w.getContent());
            vo.setCoverImageUrl(w.getCoverImageUrl());
            vo.setType(w.getType());
            vo.setAdminId(w.getAdminId());
            vo.setLookCount(w.getLookCount());
            vo.setCreateTime(w.getCreateTime());
            if(null != w.getLable()){
                Lable lable = lableService.findByIdNotNull(w.getLable());
                if(null != lable){
                    vo.setLable(lable.getLableName());
                }
            }
            Admin admin = adminService.findById(w.getAdminId());
            vo.setNickName(admin.getNickName());
            vo.setAvatarUrl(admin.getAvatarUrl());
            LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
            if (userInfoDto != null) {
                vo.setFollow(userFollowService.isFollow(userInfoDto.getUserId(), w.getAdminId()));
            }
            voList.add(vo);
        });
        return PageResponse.of(voList, filterDto.getPageable(), worksPage.getTotalElements());
    }

    public Works findById(Integer workId) {
        return worksRepository.findById(workId).orElse(null);
    }

    public Works findByIdNotNull(Integer workId) {
        return worksRepository.findById(workId).orElseThrow(() -> new EntityNotFoundException("作品不存在"));
    }

    public WorkDetailVo detail(Integer workId) {
        WorkDetailVo vo = new WorkDetailVo();
        Works works = findByIdNotNull(workId);
        vo.setId(works.getId());
        vo.setTitle(works.getTitle());
        vo.setContent(works.getContent());
        vo.setType(works.getType());
        vo.setAdminId(works.getAdminId());
        vo.setPrice(works.getPrice());
        vo.setCoverImageUrl(works.getCoverImageUrl());
        vo.setCommentCount(worksCommentService.commentCount(workId));
        vo.setCollectionCount(userCollectionService.workCollectionCount(workId));
        if(null != works.getLable()){
           Lable lable = lableService.findByIdNotNull(works.getLable());
           if(null != lable){
               vo.setLable(lable.getLableName());
           }
        }
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto != null) {
            vo.setFollow(userFollowService.isFollow(userInfoDto.getUserId(), works.getAdminId()));
            vo.setCollection(userCollectionService.isCollection(userInfoDto.getUserId(), works.getId()));
        }
        boolean isFree = works.isFree == 1;
        vo.setFree(isFree);
        if (isFree) {
            vo.setPay(true);
        } else {
            if (userInfoDto != null) {
                vo.setPay(userPayService.isPayWork(vo.getId(), vo.getAdminId(), userInfoDto.getUserId()));
            } else {
                vo.setPay(false);
            }
        }
        Admin admin = adminService.findById(works.getAdminId());
        vo.setNickName(admin.getNickName());
        vo.setAvatarUrl(admin.getAvatarUrl());
        List<String> workResource = worksResourcesService.getWorkResource(workId);
        if (workResource.size() > 0) {
            if (vo.isPay()) {
                if (works.getType().equals(1)) {
                    vo.setImagesResources(workResource);
                } else {
                    vo.setVideoResources(workResource.get(0));
                }
            } else {
                if (works.getType().equals(1)) {
                    if (workResource.size() > 3) {
                        vo.setImagesResources(workResource.subList(0, 3));
                    } else {
                        vo.setImagesResources(workResource);
                    }
                }
            }
        }
        works.setLookCount(works.getLookCount() + 1);
        WorksLookLog worksLookLog = new WorksLookLog();
        worksLookLog.setWorksId(workId);
        worksLookLog.setType(works.getType());
        worksLookLogRepository.save(worksLookLog);
        worksRepository.save(works);
        return vo;
    }

    public Integer changeStatus(Integer id) {
        Works works = findByIdNotNull(id);
        Integer status = works.getStatus();
        Integer result = status == 1 ? 0 : 1;
        works.setStatus(result);
        worksRepository.save(works);
        return result;
    }

    public void delete(Integer id) {
        Works works = findByIdNotNull(id);
        worksRepository.delete(works);
    }

    public void addWork(SaveWorkVo vo){
        SensitiveFilterService filter = SensitiveFilterService.getInstance();
        Works works = new Works();
        LoginAdminInfoDto adminInfoDto = LoginAdminThreadLocal.get();
        works.setAdminId(adminInfoDto.getAdminId());
        works.setTitle(vo.getTitle());
        works.setContent(vo.getContent());
        String title = vo.getTitle();
        if(!filter.replaceSensitiveWord(title, 1, "*").equals(title)){
            throw new ProjectException("检测到敏感词汇");
        }
        String content = vo.getContent();
        if(!filter.replaceSensitiveWord(content, 1, "*").equals(content)){
            throw new ProjectException("检测到敏感词汇");
        }
        Lable lable =lableRepository.findByLableName(vo.getLable());
        works.setLable(lable.getId());
        works.setType(vo.getType());
        works.setCoverImageUrl(vo.getCoverImageUrl());
        works.setStatus(null == adminInfoDto.getIsAudit()||(adminInfoDto.getIsAudit() == 1) ? CommonEnum.NO.getCode() : CommonEnum.YES.getCode());
        Integer isFree = (null == vo.getIsFree()) ? 1 : vo.getIsFree();
        works.setIsFree(isFree);
        if (isFree == 1) {
            works.setPrice(0);
        } else {
            works.setPrice(vo.getPrice());
        }
        Works result = worksRepository.save(works);
        List<String> resources;
        if (vo.getType().equals(1)) {
            if (StringUtils.isEmpty(vo.getGoodsImagesDetail())) {
                throw new ProjectException("至少上传一张图片");
            }
            resources = goodsImagesDetailList(vo.getGoodsImagesDetail());
            if (resources.size() < 1) {
                throw new ProjectException("至少上传一张图片");
            }
        } else {
            if (StringUtils.isEmpty(vo.getVideoDetail())) {
                throw new ProjectException("请上传视频资源");
            }
            resources = Collections.singletonList(vo.getVideoDetail());
        }
        String zipUrl = worksResourcesService.saveWorks(result.getId(), vo.getType(), resources, vo.getFolder());
        result.setZipUrl(zipUrl);
        worksRepository.save(result);
    }

    public void editWork(SaveWorkVo vo) {
        Works works = findByIdNotNull(vo.getId());
        works.setTitle(vo.getTitle());
        works.setContent(vo.getContent());
        works.setType(vo.getType());
        works.setCoverImageUrl(vo.getCoverImageUrl());
        Integer isFree = vo.getIsFree();
        works.setIsFree(isFree);
        if (isFree == 1) {
            works.setPrice(0);
        } else {
            works.setPrice(vo.getPrice());
        }
        Lable lable =lableRepository.findByLableName(vo.getLable());
        works.setLable(lable.getId());
        worksResourcesService.deleteWorks(vo.getId());
        List<String> resources;
        if (vo.getType().equals(1)) {
            if (StringUtils.isEmpty(vo.getGoodsImagesDetail())) {
                throw new ProjectException("至少上传一张图片");
            }
            resources = goodsImagesDetailList(vo.getGoodsImagesDetail());
            if (resources.size() < 1) {
                throw new ProjectException("至少上传一张图片");
            }
        } else {
            if (StringUtils.isEmpty(vo.getVideoDetail())) {
                throw new ProjectException("请上传视频资源");
            }
            resources = Collections.singletonList(vo.getVideoDetail());
        }
        String zipUrl = worksResourcesService.saveWorks(vo.getId(), vo.getType(), resources, vo.getFolder());
        works.setZipUrl(zipUrl);
        worksRepository.save(works);
    }

    public List<String> goodsImagesDetailList(String goodsImagesDetail) {
        try {
            goodsImagesDetail = goodsImagesDetail.replaceAll("\"", "");
            goodsImagesDetail = goodsImagesDetail.substring(1, goodsImagesDetail.length() - 1);
            String[] split = goodsImagesDetail.split(",");
            return Arrays.asList(split);
        } catch (Exception e) {
            throw new ProjectException("图片解析失败");
        }
    }


    public Long getAdminUpdateCount(Integer adminId) {
        return worksRepository.countAllByAdminIdAndStatus(adminId, CommonEnum.YES.getCode());
    }

    public String downloadUrl(Integer workId) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        Works works = findByIdNotNull(workId);
        if (!works.getIsFree().equals(CommonEnum.YES.getCode())) {
            boolean payWork = userPayService.isPayWork(workId, works.getAdminId(), userInfoDto.getUserId());
            if (!payWork) {
                throw new ProjectException("还未付费");
            }
        }
        UserDownloadRecord userDownloadRecord = new UserDownloadRecord();
        userDownloadRecord.setWorkId(workId);
        userDownloadRecord.setUserId(userInfoDto.getUserId());
        userDownloadRecord.setCreateTime(new Date());
        userDownloadRecordService.save(userDownloadRecord);
        return works.getZipUrl();
    }

    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }


}
