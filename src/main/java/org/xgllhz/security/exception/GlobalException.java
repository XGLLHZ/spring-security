package org.xgllhz.security.exception;

import lombok.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * @author: XGLLHZ
 * @date: 2022/7/13 23:27
 * @description: 全局异常
 */
@Data
public class GlobalException extends RuntimeException {

    private static final long serialVersionUID = 898367160864603295L;

    private static final Log logger = LogFactory.getLog(GlobalException.class);

    private String code;

    private String message;

    private Map<String, Object> data;

    public GlobalException(String message) {
        this("-1", message);
    }

    public GlobalException(Exception e) {
        this("-1", e.getMessage(), e);
    }

    public GlobalException(String message, Exception e) {
        this("-1", message, e);
    }

    public GlobalException(String code, String message) {
        this(code, message, null);
    }

    public GlobalException(String code, String message, Exception e) {
        this(code, message, e, null);
    }

    public GlobalException(String code, String message, Exception e, Map<String, Object> data) {
        super(message, e);
        logger.error(message, e);
        this.code = code;
        this.message = message;
        this.data = data;
    }

}
