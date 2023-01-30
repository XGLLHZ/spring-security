package org.xgllhz.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.xgllhz.security.util.APIResponse;
import org.xgllhz.security.util.ResponseUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: XGLLHZ
 * @date: 2022/8/7 12:05
 * @description: custom access denied handler
 */
@Slf4j
@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException, ServletException {
        log.error(exception.getMessage());
        ResponseUtils.responseJson(response, APIResponse.failure("无权访问！"));
    }

}
