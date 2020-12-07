package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.Works;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 2:18 下午
 */
@Repository
public interface WorksRepository extends JpaRepository<Works, Integer>, JpaSpecificationExecutor<Works> {

    List<Works> findAllByAdminId(Integer adminId);

    long countAllByAdminIdAndStatus(Integer adminId, Integer status);
}
