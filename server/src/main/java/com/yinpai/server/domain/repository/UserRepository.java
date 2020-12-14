package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.User;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 用户数据库操作相关
 * @author weilai
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    User findByUsernameAndPassword(String username, String password);

    User findByPhone(String phone);

    User findUserById(String Id);
}