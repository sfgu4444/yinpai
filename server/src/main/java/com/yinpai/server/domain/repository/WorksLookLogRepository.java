package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.WorksLookLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WorksLookLogRepository extends JpaRepository<WorksLookLog, Integer>, JpaSpecificationExecutor<WorksLookLog> {
}
