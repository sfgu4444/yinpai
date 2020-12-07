package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.UserPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/29 8:53 下午
 */
public interface UserPayRepository extends JpaRepository<UserPay, Integer>, JpaSpecificationExecutor<UserPay> {

    UserPay findByEntityIdAndUserIdAndType(Integer entityId, Integer userId, Integer type);
}
