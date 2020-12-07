package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/21 6:58 下午
 */
public interface ReportRepository extends JpaRepository<Report, Integer>, JpaSpecificationExecutor<Report> {
}
