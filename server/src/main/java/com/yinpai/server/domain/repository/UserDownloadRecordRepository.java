package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.UserDownloadRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/6 5:55 下午
 */
public interface UserDownloadRecordRepository extends JpaRepository<UserDownloadRecord, Integer>, JpaSpecificationExecutor<UserDownloadRecord> {

    Page<UserDownloadRecord> findAllByUserId(Integer userId, Pageable pageable);

    long countAllByWorkId(Integer workId);
}
