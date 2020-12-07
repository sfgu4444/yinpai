package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.WorksComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/30 9:23 下午
 */
public interface WorksCommentRepository extends JpaRepository<WorksComment, Integer>, JpaSpecificationExecutor<WorksComment> {

    long countAllByWorkId(Integer workId);
}
