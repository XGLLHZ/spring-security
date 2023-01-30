package org.xgllhz.security.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xgllhz.security.util.APIResponse;

import java.util.Map;

/**
 * @author: XGLLHZ
 * @date: 2022/7/13 23:38
 * @description: 全局异常处理器
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(GlobalException.class)
    public APIResponse<Map<String, Object>> handleException(GlobalException e) {
        return APIResponse.failure(e.getCode(), e.getMessage(), e.getData());
    }

}
