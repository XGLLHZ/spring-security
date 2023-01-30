package org.xgllhz.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.xgllhz.security.util.APIResponse;
import org.xgllhz.security.util.ResponseUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: XGLLHZ
 * @date: 2022/7/13 23:43
 * @description: custom authentication failure handler
 */
@Component
@Slf4j
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        log.error(exception.getMessage());

        String message;
        if (exception instanceof UsernameNotFoundException || exception instanceof BadCredentialsException) {
            message = "Wrong account or password！";
        } else if (exception instanceof LockedException) {
            message = "Account be locked！";
        } else if (exception instanceof DisabledException) {
            message = "Account be disabled！";
        } else if (exception instanceof AccountExpiredException) {
            message = "Account has expired！";
        } else if (exception instanceof CredentialsExpiredException) {
            message = "Password has expired！";
        } else {
            message = exception.getMessage();
        }

        ResponseUtils.responseJson(response, APIResponse.failure(message));
    }

}
