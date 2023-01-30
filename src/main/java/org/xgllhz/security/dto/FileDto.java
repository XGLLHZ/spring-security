package org.xgllhz.security.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: XGLLHZ
 * @date: 2022/11/22 10:38
 * @description: file dto
 */
@Data
@ToString
@EqualsAndHashCode
public class FileDto implements Serializable {

    private static final long serialVersionUID = -5801288858212909126L;

    private String originalName;   //文件原名

    private String fileName;   //文件名

    private String fileType;   //文件类型

    private String fileSize;   //文件大小

    private String filePath;   //文件路径

}
