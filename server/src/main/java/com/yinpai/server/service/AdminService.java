package com.yinpai.server.service;

import com.yinpai.server.domain.dto.LoginAdminInfoDto;
import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.entity.admin.Admin;
import com.yinpai.server.domain.repository.admin.AdminRepository;
import com.yinpai.server.enums.CommonEnum;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.exception.ProjectException;
import com.yinpai.server.service.UserFollowService;
import com.yinpai.server.service.UserPayService;
import com.yinpai.server.thread.threadlocal.LoginAdminThreadLocal;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import com.yinpai.server.utils.CookieUtil;
import com.yinpai.server.utils.ProjectUtil;
import com.yinpai.server.vo.AdminHomeProfileVo;
import com.yinpai.server.vo.admin.AdminAddForm;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 4:35 下午
 */
@Service
public class AdminService {

    private final AdminRepository adminRepository;

    private final UserFollowService userFollowService;

    private final UserPayService userPayService;

    private final ConcurrentHashMap<String, String> userMap = new ConcurrentHashMap<>();

    public final static List<Integer> superAdminIds = Arrays.asList(1, 2);

    @Autowired
    public AdminService(AdminRepository adminRepository, UserFollowService userFollowService, @Lazy UserPayService userPayService) {
        this.adminRepository = adminRepository;
        this.userFollowService = userFollowService;
        this.userPayService = userPayService;
    }

    public Page<Admin> findFilterAll(Map<String, String> map, Pageable pageable) {
        LoginAdminInfoDto adminInfoDto = LoginAdminThreadLocal.get();
        if (adminInfoDto.isSuperAdmin()) {
            return adminRepository.findAll(ProjectUtil.getSpecification(map), pageable);
        } else {
           return adminRepository.findAllById(adminInfoDto.getAdminId(), pageable);
        }
    }

    public Admin findByIdNotNull(Integer adminId) {
        return adminRepository.findById(adminId).orElseThrow(() -> new ProjectException("商户不存在"));
    }

    public Admin findById(Integer adminId) {
        return adminRepository.findById(adminId).orElseGet(Admin::new);
    }

    public Admin login(String adminName, String adminPass) {
        Admin admin = adminRepository.findByAdminNameAndAdminPass(adminName, ProjectUtil.password(adminPass));
        if (admin == null) {
            throw new ProjectException("用户名或者密码不正确");
        }
        if (!admin.getAdminStatus().equals(CommonEnum.YES.getCode())) {
            throw new ProjectException("账号被停用");
        }
        String token = UUID.randomUUID().toString();
        userMap.put(String.format("weilai_%s", token), admin.getId().toString());
        CookieUtil.set(ProjectUtil.getResponse(), "token", token, 86400);
        admin.setLastTime(new Date());
        admin.setLastIp(ProjectUtil.getIpAddr());
        try {
            return adminRepository.save(admin);
        } catch (Exception e) {
            return admin;
        }
    }

    public Admin getLoginAdmin(String token) {
        String adminId = userMap.get(String.format("weilai_%s", token));
        return findByIdNotNull(Integer.valueOf(adminId));
    }

    public Admin add(AdminAddForm adminAddForm) {
        Admin info = adminRepository.findByAdminName(adminAddForm.getAdminName());
        if (info != null) {
            throw new ProjectException("用户已经存在");
        }
        Admin adminEntity = new Admin();
        BeanUtils.copyProperties(adminAddForm, adminEntity);
        adminEntity.setCreateTime(new Date());
        adminEntity.setUpdateTime(new Date());
        adminEntity.setMoney(0);
        adminEntity.setAdminPass(ProjectUtil.password(adminAddForm.getAdminPass()));
        adminEntity.setAdminStatus(CommonEnum.YES.getCode());
        adminEntity.setBackgroundUrl(adminAddForm.getBackgroundUrl());
        try {
            return adminRepository.save(adminEntity);
        } catch (Exception e) {
            throw new ProjectException("用户操作失败");
        }
    }

    public void edit(AdminAddForm adminAddForm) {
        Admin ad = findByIdNotNull(adminAddForm.getId());
        if (!ad.getAdminName().equals(adminAddForm.getAdminName())) {
            Admin info = adminRepository.findByAdminName(adminAddForm.getAdminName());
            if (info != null) {
                throw new ProjectException("用户已经存在");
            }
        }
        if (adminAddForm.getAdminPass() != null) {
            ad.setAdminPass(adminAddForm.getAdminPass());
        }
        ad.setAdminName(adminAddForm.getAdminName());
        ad.setAvatarUrl(adminAddForm.getAvatarUrl());
        ad.setBackgroundUrl(adminAddForm.getBackgroundUrl());
        ad.setNickName(adminAddForm.getNickName());
        ad.setMonthPayPrice(adminAddForm.getMonthPayPrice());
        ad.setQuarterPayPrice(adminAddForm.getQuarterPayPrice());
        ad.setYearPayPrice(adminAddForm.getYearPayPrice());
        adminRepository.save(ad);
    }


    public void logout() {
        Cookie cookie = CookieUtil.get(ProjectUtil.getRequest(), "token");
        if (cookie == null) {
            return;
        }
        userMap.remove(String.format("weilai_%s", cookie.getValue()));

        CookieUtil.set(ProjectUtil.getResponse(), "token", null, 0);
    }

    public Integer changeAdminStatus(Integer id) {
        Admin admin = findByIdNotNull(id);
        Integer adminStatus = admin.getAdminStatus();
        Integer result = adminStatus == 1 ? 0 : 1;
        admin.setAdminStatus(result);
        adminRepository.save(admin);
        return result;
    }

    public void delete(Integer id) {
        Admin admin = findByIdNotNull(id);
        adminRepository.delete(admin);
    }

    public void changeAudit(Integer id, Integer isAudit) {
        Admin admin = findByIdNotNull(id);
        admin.setIsAudit(isAudit);
        adminRepository.save(admin);
    }

    public AdminHomeProfileVo adminHomeProfile(Integer adminId) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        Admin admin = findByIdNotNull(adminId);
        AdminHomeProfileVo vo = new AdminHomeProfileVo();
        vo.setNickName(admin.getNickName());
        vo.setAvatarUrl(admin.getAvatarUrl());
        vo.setBackgroundUrl(admin.getBackgroundUrl());
        vo.setFansNumber(userFollowService.adminFansNumber(adminId));
        vo.setJoinVip(userPayService.isPayAdmin(adminId, userInfoDto.getUserId()));
        vo.setFollow(userFollowService.isFollow(userInfoDto.getUserId(), adminId));
        return vo;
    }
}
