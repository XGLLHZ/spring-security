package org.xgllhz.security.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.xgllhz.security.config.YmlConfig;
import org.xgllhz.security.dto.FileDto;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: XGLLHZ
 * @date: 2022/11/22 10:34
 * @description:
 */
public class FileUtils extends FileUtil {

    //文件类型
    private static final String DOCUMENT = ".doc .docx .pdf .pps .ppt .txt .xls .xlsx";
    private static final String MUSIC = ".aac .aif .mpa .mp3 .m4a .ra .ram .wav .wma";
    private static final String IMAGE = ".bmp .cdr .dib .dif .eps .gif .iff .jpeg .jpg .mpt .pcd .pcp .png .psd .tga .tif .wmf";
    private static final String VIDEO = ".asf .avi .flv .mov .mpe .mpeg .mpg .mp4 .m4v .ogg .ogv .qt .rm .webm .wmv";

    /**
     * 上传文件
     * @param multipartFile MultipartFile (web 中用来接收前台传来的文件)
     * @return
     */
    public static FileDto uploadFile(MultipartFile multipartFile) {
        //创建阿里云客户端对象
        OSSClient ossClient = new OSSClient(YmlConfig.getEndPoint(), YmlConfig.getKeyId(), YmlConfig.getKeySecret());
        //设置读写权限
        ossClient.setBucketAcl(YmlConfig.getBucket(), CannedAccessControlList.PublicRead);
        FileDto fileEntity = new FileDto();
        // 获取文件名
        String fileName = multipartFile.getOriginalFilename();
        // 获取文件类型
        String fileType = "." + getPrefix(fileName);
        try {
            //生成文件名
            String fileNewName = generateFileName(fileType);
            // 将 MultipartFile 转化成 File
            File file = toFile(multipartFile);
            if (file.exists()) {
                ObjectMetadata objectMetadata = new ObjectMetadata();
                InputStream inputStream = new FileInputStream(file);
                //设置文件类型
                objectMetadata.setContentType(getContentType(fileNewName.substring(fileNewName.lastIndexOf("."))));
                objectMetadata.setContentLength(inputStream.available());
                //上传文件
                PutObjectResult putObjectResult = ossClient.putObject(YmlConfig.getBucket(),
                        YmlConfig.getFileDir() + getFileDir(fileType) + fileNewName, inputStream, objectMetadata);
                //判断结果，并将文件信息返回
                if (StringUtils.isNotEmpty(putObjectResult.getETag())) {
                    fileEntity.setOriginalName(fileName);
                    fileEntity.setFileName(fileNewName);
                    fileEntity.setFileType(getFileType(fileType));
                    fileEntity.setFileSize(file.length() + "");
                    fileEntity.setFilePath(YmlConfig.getImageUrl() + fileNewName);
                    return fileEntity;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ossClient.shutdown();
        return null;
    }

    /**
     * 判断文件上传位置（阿里云位置）
     * @param type
     * @return
     */
    public static String getFileDir(String type) {
        if (DOCUMENT.contains(type)) {
            return "documents/";
        } else if (MUSIC.contains(type)) {
            return "musics/";
        } else if (IMAGE.contains(type)) {
            return "images/";
        } else if (VIDEO.contains(type)) {
            return "videos/";
        } else {
            return "others/";
        }
    }

    /**
     * 判断文件类型
     * @param type
     * @return
     */
    public static String getFileType(String type) {
        if (DOCUMENT.contains(type)) {
            return "文档";
        } else if (MUSIC.contains(type)) {
            return "音频";
        } else if (IMAGE.contains(type)) {
            return "图片";
        } else if (VIDEO.contains(type)) {
            return "视频";
        } else {
            return "其它";
        }
    }

    /**
     * 生成文件名
     * @param fileType 文件类型
     * @return
     */
    public static String generateFileName(String fileType) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = simpleDateFormat.format(new Date());
        return date + fileType;
    }

    /**
     * 根据文件名获取文件路径
     * @param fileName
     * @return
     */
    public static String getFileUrl(String fileName) {
        OSSClient ossClient = new OSSClient(YmlConfig.getEndPoint(), YmlConfig.getKeyId(), YmlConfig.getKeySecret());
        //链接有效期 9h
        Date expiration = new Date(new Date().getTime() + 9 * 60 * 60);
        //获取文件后缀
        int lastIndexOf = fileName.lastIndexOf(".");
        String fileType = fileName.substring(lastIndexOf);
        return ossClient.generatePresignedUrl(YmlConfig.getBucket(),YmlConfig.getFileDir() + getFileDir(fileType),
                expiration).toString();
    }

    /**
     * inputStream 转成 File
     * @param inputStream
     * @param name
     * @return
     * @throws Exception
     */
    public static File inputStreamToFile(InputStream inputStream, String name) throws Exception {
        //System.getProperty("java.io.tmpdir")：获取当前系统缓存临时目录
        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + name);
        if (file.exists()) {
            return file;
        }
        OutputStream outputStream = new FileOutputStream(file);
        int bytes;
        int len = 8192;
        byte[] bytes1 = new byte[len];
        while ((bytes = inputStream.read(bytes1, 0, len)) != -1) {
            outputStream.write(bytes1, 0, bytes);
        }
        outputStream.close();
        inputStream.close();
        return file;
    }

    /**
     * MultipartFile to File
     * @param multipartFile
     * @return
     */
    public static File toFile(MultipartFile multipartFile){
        // 获取文件名
        String fileName = multipartFile.getOriginalFilename();
        // 获取文件后缀
        String prefix = "." + getPrefix(fileName);
        File file = null;
        try {
            // 用uuid作为文件名，防止生成的临时文件重复
            file = File.createTempFile(IdUtil.simpleUUID(), prefix);
            // MultipartFile to File
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 获取文件后缀 不带 .
     * @param fileName
     * @return
     */
    public static String getPrefix(String fileName) {
        if ((fileName != null) && (fileName.length() > 0)) {
            int dot = fileName.lastIndexOf('.');
            if ((dot >-1) && (dot < (fileName.length() - 1))) {
                return fileName.substring(dot + 1);
            }
        }
        return fileName;
    }

    /**
     * 设置文件类型 解决文件 url 打开为下载而不是直接展示的问题
     * @param FilenameExtension
     * @return
     */
    public static String getContentType(String FilenameExtension) {
        if (FilenameExtension.equalsIgnoreCase(".bmp")) {
            return "image/bmp";
        }
        if (FilenameExtension.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        if (FilenameExtension.equalsIgnoreCase(".jpeg") ||
                FilenameExtension.equalsIgnoreCase(".jpg") ||
                FilenameExtension.equalsIgnoreCase(".png")) {
            return "image/jpg";
        }
        if (FilenameExtension.equalsIgnoreCase(".html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase(".txt")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase(".vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase(".pptx") ||
                FilenameExtension.equalsIgnoreCase(".ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase(".docx") ||
                FilenameExtension.equalsIgnoreCase(".doc")) {
            return "application/msword";
        }
        if (FilenameExtension.equalsIgnoreCase(".xml")) {
            return "text/xml";
        }
        return "image/jpg";
    }

    public static void main(String[] args) throws IOException {
        File file = new File("/Users/xgllhz/Documents/project/github/tmp/baiyicuo.jpg");
        FileInputStream stream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", stream);
        FileDto fileDto = uploadFile(multipartFile);
        System.out.println(fileDto.toString());
    }

}
