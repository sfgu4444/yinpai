package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.UserCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/5 10:01 下午
 */
@Repository
public interface UserCollectionRepository extends JpaRepository<UserCollection, Integer>, JpaSpecificationExecutor<UserCollection> {

    UserCollection findByUserIdAndWorkId(Integer userId, Integer workId);

    Long findAllByUserId(Integer userId);

    long countAllByUserId(Integer userId);

    long countAllByWorkId(Integer workId);
}
