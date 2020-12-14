package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.UserRechargeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 *  JpaRepository:基本的CRUD
 *  UserRechargeRecord:操作表对应的实体类
 *  Integer:表中主键对应实体类中的属性类型
 *
 *  JpaSpecificationExecutor:复杂的SQL
 */
public interface UserRechargeRecordRepository extends JpaRepository<UserRechargeRecord, Integer>, JpaSpecificationExecutor<UserRechargeRecord> {
}
