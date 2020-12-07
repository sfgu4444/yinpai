package com.yinpai.server.domain.repository.admin;

import com.yinpai.server.domain.entity.admin.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 4:37 下午
 */
public interface AdminRepository extends JpaRepository<Admin, Integer>, JpaSpecificationExecutor<Admin> {

    Admin findByAdminNameAndAdminPass(String adminName, String adminPass);

    Admin findByAdminName(String adminName);

    Page<Admin> findAllById(Integer adminId, Pageable pageable);
}
