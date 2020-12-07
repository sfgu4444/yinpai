package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.OriginalApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/6 12:39 上午
 */
public interface OriginalApplyRepository extends JpaRepository<OriginalApply, Integer>, JpaSpecificationExecutor<OriginalApply> {
}
