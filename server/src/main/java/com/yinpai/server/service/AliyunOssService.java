package com.yinpai.server.service;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.*;
import com.yinpai.server.config.OssConfig;
import com.yinpai.server.controller.admin.WorkController;
import com.yinpai.server.utils.CompressUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2019-06-14 18:12
 */
@Slf4j
@Component
public class AliyunOssService {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final String ENDPOINT;

    private final String BUCKET_NAME;

    private final OSSClient ossClient;

    private final WorkController workController1;

    @Autowired
    public AliyunOssService(OssConfig ossConfig, WorkController workController1) {
        ENDPOINT = ossConfig.getEndpointOuter();
        BUCKET_NAME = ossConfig.getBucketName();
        this.workController1 = workController1;
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        this.ossClient = new OSSClient(ENDPOINT, credentialsProvider, clientConfiguration);
    }

    /**
     * 创建存储空间
     *
     * @param bucketName 存储空间
     */
    public String createBucketName(String bucketName) {
        // 存储空间
        if (!ossClient.doesBucketExist(bucketName)) {
            // 创建存储空间
            Bucket bucket = ossClient.createBucket(bucketName);
            log.info("创建存储空间成功");
            return bucket.getName();
        }
        return bucketName;
    }

    /**
     * 删除存储空间buckName
     *
     * @param bucketName 存储空间
     */
    public void deleteBucket(String bucketName) {
        ossClient.deleteBucket(bucketName);
        log.info("删除" + bucketName + "Bucket成功");
    }

    /**
     * 创建模拟文件夹
     *
     * @param bucketName 存储空间
     * @param folder     模拟文件夹名如"qj_nanjing/"
     * @return 文件夹名
     */
    public String createFolder(String bucketName, String folder) {
        // 文件夹名
        // 判断文件夹是否存在，不存在则创建
        if (!ossClient.doesObjectExist(bucketName, folder)) {
            // 创建文件夹
            ossClient.putObject(bucketName, folder, new ByteArrayInputStream(new byte[0]));
            log.info("创建文件夹成功");
            // 得到文件夹名
            OSSObject object = ossClient.getObject(bucketName, folder);
            return object.getKey();
        }
        return folder;
    }

    /**
     * 根据key删除OSS服务器上的文件
     *
     * @param bucketName 存储空间
     * @param folder     模拟文件夹名 如"qj_nanjing/"
     * @param key        Bucket下的文件的路径名+文件名 如："upload/cake.jpg"
     */
    public void deleteFile(String bucketName, String folder, String key) {
        ossClient.deleteObject(bucketName, folder + key);
        log.info("删除" + bucketName + "下的文件" + folder + key + "成功");
    }

    public void deleteFile(String key) {
        ossClient.deleteObject(BUCKET_NAME, key);
        log.info("删除" + BUCKET_NAME + "下的文件" + key + "成功");
    }

    public void deleteFileByUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return;
        }
        URL url1 = null;
        try {
            url1 = new URL(url);
        } catch (MalformedURLException e) {
            log.error("删除云端文件失败", e);
        }
        if (url1 == null) {
            return;
        }
        String key = url1.getPath().substring(1);
        deleteFile(key);
    }

    /**
     * 上传图片至OSS
     *
     * @param file 上传文件（文件全路径如：D:\\image\\cake.jpg）
     * @return String url
     */
    public String uploadObject2OSS(File file, String folder) throws IOException {
        // 创建OSSClient实例
        String resultStr = null;
        try (InputStream is = new FileInputStream(file)) {
            // 以输入流的形式上传文件
            // 文件名
            String fileName = file.getName();
            // 文件大小
            long fileSize = file.length();
            // 创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            // 上传的文件的长度
            metadata.setContentLength(is.available());
            // 指定该Object被下载时的网页的缓存行为
            metadata.setCacheControl("no-cache");
            // 指定该Object下设置Header
            metadata.setHeader("Pragma", "no-cache");
            // 指定该Object被下载时的内容编码格式
            metadata.setContentEncoding("utf-8");
            // 文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
            // 如果没有扩展名则填默认值application/octet-stream
            metadata.setContentType(getContentType(fileName));
            // 指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
            metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
            // 上传文件   (上传文件流的形式)
            PutObjectResult putResult = ossClient.putObject(BUCKET_NAME, folder + fileName, is, metadata);
            // 解析结果
            return "http://" + BUCKET_NAME + "." + ENDPOINT + "/" + folder + fileName;
        } catch (Exception e) {
            log.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }
        return resultStr;
    }

    /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     *
     * @param fileName 文件名
     * @return 文件的contentType
     */
    public static String getContentType(String fileName) {
        // 文件的后缀名
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        if (".bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if (".gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if (".jpeg".equalsIgnoreCase(fileExtension)
                || ".jpg".equalsIgnoreCase(fileExtension)
                || ".png".equalsIgnoreCase(fileExtension)) {
            return "image/jpg";
        }
        if (".html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        }
        if (".txt".equalsIgnoreCase(fileExtension)) {
            return "text/plan";
        }
        if (".vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        }
        if (".ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if (".doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if (".mp4".equalsIgnoreCase(fileExtension)) {
            return "video/mp4";
        }
        if (".xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        }
        if (".zip".equalsIgnoreCase(fileExtension)) {
            return "application/x-zip-compressed";
        }
        // 默认返回类型
        return "image/jpg";
    }

    /**
     * 上传图片并返回url
     *
     * @param uploadFile
     */
    public String uploadImageToOSSReturnPresignedUrl(MultipartFile uploadFile, String fileName, String folder)
            throws Exception {
        ossClient(fileName, uploadFile.getBytes(), folder);
        // 返回URL
        return getUrl(folder + fileName);
    }

    public String uploadImageToOSSReturnUrl(MultipartFile uploadFile, String fileName, String folder)
            throws IOException {
        ossClient(fileName, uploadFile.getBytes(), folder);
        // 返回URL
        return "http://" + BUCKET_NAME + "." + ENDPOINT + "/" + folder + fileName;
    }

    public String uploadImageToOSSReturnKey(MultipartFile uploadFile, String fileName, String folder)
            throws Exception {
        ossClient(fileName, uploadFile.getBytes(), folder);
        // 返回key
        return folder + fileName;
    }

    /**
     * 上传文件并返回 url
     *
     * @param bytes    文件字节数组
     * @param fileName 文件名
     * @return
     * @throws Exception
     */
    public String uploadBytesToOSSReturnPresignedUrl(byte[] bytes, String fileName, String folder)
            throws Exception {
        ossClient(fileName, bytes, folder);
        // 返回URL
        return getUrl(folder + fileName);
    }

    public String uploadBytesToOSSReturnUrl(byte[] bytes, String fileName, String folder) throws Exception {
        ossClient(fileName, bytes, folder);
        // 返回URL
        return "http://" + BUCKET_NAME + "." + ENDPOINT + "/" + folder + fileName;
    }

    public String uploadBytesToOSSReturnKey(byte[] bytes, String fileName, String folder) throws Exception {
        ossClient(fileName, bytes, folder);
        // 返回key
        return folder + fileName;
    }

    /**
     * 构建 ossClient, 并上传文件
     *
     * @param fileName
     * @param bytes
     */
    public void ossClient(String fileName, byte[] bytes, String folder) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectMetadata metadata = new ObjectMetadata();
        // 上传的文件的长度
        metadata.setContentLength(byteArrayInputStream.available());
        // 指定该Object被下载时的网页的缓存行为
//        metadata.setCacheControl("no-cache");
        // 指定该Object下设置Header
//        metadata.setHeader("Pragma", "no-cache");
        // 指定该Object被下载时的内容编码格式
        metadata.setContentEncoding("utf-8");
        // 文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
        // 如果没有扩展名则填默认值application/octet-stream
        metadata.setContentType(getContentType(fileName));
        // 指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
        metadata.setContentDisposition("filename/filesize=" + fileName + "/" + bytes.length + "Byte.");

        // 上传
        ossClient.putObject(BUCKET_NAME, folder + fileName, byteArrayInputStream, metadata);
    }

    public String getUrl(String key) {
        if (key.startsWith("http")) {
            return key;
        }
        // 7天有效期
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 7);
        return ossClient.generatePresignedUrl(BUCKET_NAME, key, expiration, HttpMethod.GET).toString();
    }

    public String download(String folder) {
        String prefix = "yinpai/" + LocalDateTime.now().format(formatter) + "/"  + folder + "/";
        ObjectListing objectListing = ossClient.listObjects(BUCKET_NAME, prefix);
        String localPath = "/tmp/yinpai/" + folder;
        for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            //key：fun/like/001.avi等，即：Bucket中存储文件的路径
            String key = objectSummary.getKey();
            //判断文件所在本地路径是否存在，若无，新建目录
            File file = new File(localPath + "/" + key.substring(key.lastIndexOf("/") + 1));
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            //下载object到文件
            ossClient.getObject(new GetObjectRequest(BUCKET_NAME, key), file);
        }
        File fileFolder = new File(localPath);
        try {
            CompressUtils.zipFile(fileFolder, "zip");
            File fileZip = new File( localPath + ".zip");
            String url = uploadObject2OSS(fileZip, prefix);
            fileZip.delete();
            deleteDir(localPath);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteDir(String dirPath) {
        File file = new File(dirPath);
        if (file.isFile()) {
            file.delete(); // 删除文件
        } else {
            File[] files = file.listFiles();
            if (files == null) {
                file.delete(); // 删除空文件夹
            } else {
                for (File f : files) {
                    deleteDir(f.getAbsolutePath()); // 迭代删除非空文件夹
                }
                file.delete();
            }
        }
    }

}
