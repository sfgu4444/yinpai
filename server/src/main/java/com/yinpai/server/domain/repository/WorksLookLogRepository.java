package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.WorksLookLog;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.awt.*;

public interface WorksLookLogRepository extends JpaRepository<WorksLookLog, Integer>, JpaSpecificationExecutor<WorksLookLog> {


}
