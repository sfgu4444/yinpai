package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 用户数据库操作相关
 * @author weilai
 */
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    User findByUsernameAndPassword(String username, String password);

    User findByPhone(String phone);

    User findUserById(Integer Id);
}