package org.xgllhz.security.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.xgllhz.security.entity.UserEntity;
import org.xgllhz.security.service.IUserService;
import org.xgllhz.security.util.APIResponse;
import org.xgllhz.security.util.ResponseUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: XGLLHZ
 * @date: 2022/7/13 22:54
 * @description: custom authentication success handler
 */
@Component
@RequiredArgsConstructor
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final IUserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        userEntity.setPassword(null);
        userEntity.setPermTree(userService.obtainUserPermTree(userEntity));
        ResponseUtils.responseJson(response, APIResponse.success("Login successful!", userEntity));
    }

}
