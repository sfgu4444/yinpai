package com.yinpai.server.domain.repository.admin;

import com.yinpai.server.domain.entity.Works;
import com.yinpai.server.domain.entity.admin.Withdrawal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Integer>, JpaSpecificationExecutor<Withdrawal> {

    Page<Withdrawal> findAllByAdminId(Integer adminId, Pageable pageable);

}
