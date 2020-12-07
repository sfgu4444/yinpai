package com.yinpai.server.service;

import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.BaseFilterDto;
import com.yinpai.server.domain.entity.UserDownloadRecord;
import com.yinpai.server.domain.entity.Works;
import com.yinpai.server.domain.repository.UserDownloadRecordRepository;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import com.yinpai.server.vo.UserDownloadListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/6 5:56 下午
 */
@Service
public class UserDownloadRecordService {

    private final UserDownloadRecordRepository userDownloadRecordRepository;

    private final WorksService worksService;

    @Autowired
    public UserDownloadRecordService(UserDownloadRecordRepository userDownloadRecordRepository, @Lazy WorksService worksService) {
        this.userDownloadRecordRepository = userDownloadRecordRepository;
        this.worksService = worksService;
    }

    public UserDownloadRecord save(UserDownloadRecord userDownloadRecord) {
        return userDownloadRecordRepository.save(userDownloadRecord);
    }

    public PageResponse<UserDownloadListVo> findAll(BaseFilterDto baseFilterDto) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        Page<UserDownloadRecord> downloadRecordPage = userDownloadRecordRepository.findAllByUserId(userInfoDto.getUserId(), baseFilterDto.getSetPageable());
        List<UserDownloadListVo> voList = new ArrayList<>();
        downloadRecordPage.forEach( d -> {
            UserDownloadListVo vo = new UserDownloadListVo();

            Works works = worksService.findById(d.getWorkId());
            if (works != null) {
                vo.setWorkId(d.getWorkId());
                vo.setTitle(works.getTitle());
                vo.setType(works.getType());
                voList.add(vo);
            }
        });
        return PageResponse.of(voList, baseFilterDto.getPageable(), downloadRecordPage.getTotalElements());
    }

    public Long workDownloadCount(Integer workId) {
        return userDownloadRecordRepository.countAllByWorkId(workId);
    }
}
