package com.yinpai.server.domain.repository.admin;

import com.yinpai.server.domain.entity.admin.Admin;
import com.yinpai.server.domain.entity.admin.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MessageRepository  extends JpaRepository<Message, Integer>, JpaSpecificationExecutor<Message> {

    Page<Message> findAllByReceive(Integer reveive, Pageable pageable);

}
