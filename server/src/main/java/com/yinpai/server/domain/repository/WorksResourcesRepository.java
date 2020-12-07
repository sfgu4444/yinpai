package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.WorksResources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 2:57 下午
 */
public interface WorksResourcesRepository extends JpaRepository<WorksResources, Integer>, JpaSpecificationExecutor<WorksResources> {

    List<WorksResources> findAllByWorkId(Integer workId);

    void deleteAllByWorkId(Integer workId);
}
