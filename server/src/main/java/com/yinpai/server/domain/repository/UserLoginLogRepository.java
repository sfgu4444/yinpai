package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.UserLoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserLoginLogRepository extends JpaRepository<UserLoginLog, Integer>, JpaSpecificationExecutor<UserLoginLog> {
}
