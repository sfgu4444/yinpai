package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.Lable;
import com.yinpai.server.domain.entity.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LableRepository extends JpaRepository<Lable, Integer>, JpaSpecificationExecutor<Lable> {

    Lable findByLableName(String lableName);
}
