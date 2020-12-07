package com.yinpai.server.service;

import com.yinpai.server.domain.entity.WorksResources;
import com.yinpai.server.domain.repository.WorksResourcesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 2:58 下午
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WorksResourcesService {

    private final WorksResourcesRepository worksResourcesRepository;

    private final AliyunOssService aliyunOssService;

    @Autowired
    public WorksResourcesService(WorksResourcesRepository worksResourcesRepository, @Lazy AliyunOssService aliyunOssService) {
        this.worksResourcesRepository = worksResourcesRepository;
        this.aliyunOssService = aliyunOssService;
    }

    public List<String> getWorkResource(Integer workId) {
        List<WorksResources> worksImages = worksResourcesRepository.findAllByWorkId(workId);
        return worksImages.stream().map(WorksResources::getFileUrl).collect(Collectors.toList());
    }

    public void deleteWorks(Integer workId) {
        worksResourcesRepository.deleteAllByWorkId(workId);
    }

    public String saveWorks(Integer workId, Integer type, List<String> fileUrl, String folder) {
        if (fileUrl.size() < 1) {
            return null;
        }

        List<WorksResources> worksResourcesList = new ArrayList<>();
        fileUrl.forEach( f -> {
            WorksResources worksResources = new WorksResources();
            worksResources.setWorkId(workId);
            worksResources.setType(type);
            worksResources.setFileUrl(f);
            worksResourcesList.add(worksResources);
        });
        worksResourcesRepository.saveAll(worksResourcesList);

        return aliyunOssService.download(folder);
    }
}
