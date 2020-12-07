package com.yinpai.server.controller.app;

import com.yinpai.server.annotation.IgnoreCommonResponse;
import com.yinpai.server.exception.MethodArgumentCheckException;
import com.yinpai.server.exception.ProjectException;
import com.yinpai.server.service.AliyunOssService;
import com.yinpai.server.utils.ResultUtil;
import com.yinpai.server.vo.admin.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 11:00 下午
 */
@RestController
public class UploadController {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final AliyunOssService aliyunOssService;

    @Autowired
    public UploadController(AliyunOssService aliyunOssService) {
        this.aliyunOssService = aliyunOssService;
    }

    @PostMapping("/oss/upload")
    @IgnoreCommonResponse
    public ResultVO upload(@RequestParam(value = "file", required = false) MultipartFile file) {
        if (file == null) {
            throw new MethodArgumentCheckException("未获取到文件信息");
        }
        try {
            String originalFilename = file.getOriginalFilename();
            String fileName = StringUtils.isEmpty(originalFilename) ? file.getName() : originalFilename;
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            fileName = LocalDateTime.now().format(formatter) + "/" + UUID.randomUUID() + new Random().nextInt(999999) + suffix;
            return ResultUtil.successData(aliyunOssService.uploadBytesToOSSReturnUrl(file.getBytes(), fileName, "yinpai/"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(500, e.getMessage());
        }
    }

    @PostMapping("/oss/works/upload")
    public ResultVO uploadWorks(@RequestParam("folder") Long folder, @RequestParam(value = "file", required = false) MultipartFile file) {
        if (file == null) {
            throw new MethodArgumentCheckException("未获取到文件信息");
        }
        try {
            String originalFilename = file.getOriginalFilename();
            String fileName = StringUtils.isEmpty(originalFilename) ? file.getName() : originalFilename;
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            fileName = LocalDateTime.now().format(formatter) + "/"  + folder + "/" + UUID.randomUUID() + new Random().nextInt(999999) + suffix;
            return ResultUtil.successData(aliyunOssService.uploadBytesToOSSReturnUrl(file.getBytes(), fileName, "yinpai/"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(500, e.getMessage());
        }
    }

    @PostMapping("/oss/delete")
    public ResultVO delete(@RequestParam(value = "url", required = false) String url) {
        if (StringUtils.isEmpty(url)) {
            throw new ProjectException("获取url失败");
        }
        try {
            aliyunOssService.deleteFileByUrl(url);
            return ResultUtil.success(url);
        } catch (Exception e) {
            throw new ProjectException(e.getMessage());
        }
    }
}
