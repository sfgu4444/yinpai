package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserOrderRepository extends JpaRepository<UserOrder, Long>, JpaSpecificationExecutor<UserOrder> {


}
