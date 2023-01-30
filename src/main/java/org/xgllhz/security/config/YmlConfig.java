package org.xgllhz.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author: XGLLHZ
 * @date: 2022/11/22 10:04
 * @description: yml config
 */
@Configuration
public class YmlConfig {

    /** security */
    public static Long redisExpirationTimeout;   // redis 认证信息有效时间 单位 s

    /** file */
    public static String fileAvatarDir;   // avatar dir

    /** oss */
    public static String endPoint;  // end point

    public static String keyId;   // access key id

    public static String keySecret;   // access key secret

    public static String bucket;   // bucket

    public static String fileDir;   // 文件上传基路径

    public static String imageUrl;   // 图片路径

    @Value("${app.security.redis-expiration-timeout}")
    public void setRedisExpirationTimeout(Long redisExpirationTimeout) {
        YmlConfig.redisExpirationTimeout = redisExpirationTimeout;
    }

    @Value("${app.file.avatar-dir}")
    public void setFileAvatarDir(String fileAvatarDir) {
        YmlConfig.fileAvatarDir = fileAvatarDir;
    }

    @Value("${app.oss.end-point}")
    public void setEndPoint(String endPoint) {
        YmlConfig.endPoint = endPoint;
    }

    @Value("${app.oss.key-id}")
    public void setKeyId(String keyId) {
        YmlConfig.keyId = keyId;
    }

    @Value("${app.oss.key-secret}")
    public void setKeySecret(String keySecret) {
        YmlConfig.keySecret = keySecret;
    }

    @Value("${app.oss.bucket}")
    public void setBucket(String bucket) {
        YmlConfig.bucket = bucket;
    }

    @Value("${app.oss.file-dir}")
    public void setFileDir(String fileDir) {
        YmlConfig.fileDir = fileDir;
    }

    @Value("${app.oss.image-url}")
    public void setImageUrl(String imageUrl) {
        YmlConfig.imageUrl = imageUrl;
    }

    public static Long getRedisExpirationTimeout() {
        return redisExpirationTimeout;
    }

    public static String getFileAvatarDir() {
        return fileAvatarDir;
    }

    public static String getEndPoint() {
        return endPoint;
    }

    public static String getKeyId() {
        return keyId;
    }

    public static String getKeySecret() {
        return keySecret;
    }

    public static String getBucket() {
        return bucket;
    }

    public static String getFileDir() {
        return fileDir;
    }

    public static String getImageUrl() {
        return imageUrl;
    }

}
