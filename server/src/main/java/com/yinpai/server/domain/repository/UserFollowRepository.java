package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 2:42 下午
 */
public interface UserFollowRepository extends JpaRepository<UserFollow, Integer>, JpaSpecificationExecutor<UserFollow> {

    UserFollow findByUserIdAndAdminId(Integer userId, Integer adminId);

    long countAllByAdminId(Integer adminId);

    long countAllByUserId(Integer userId);

    List<UserFollow> findAllByUserId(Integer userId);
}
