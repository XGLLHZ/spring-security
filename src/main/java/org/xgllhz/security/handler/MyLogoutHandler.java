package org.xgllhz.security.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.xgllhz.security.config.MySecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: XGLLHZ
 * @date: 2022/8/18 20:41
 * @description: custom logout handler
 */
@Component
@RequiredArgsConstructor
public class MyLogoutHandler implements LogoutHandler {

    private final MySecurityContextRepository securityContextRepository;

    private final SessionRegistry sessionRegistry;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        sessionRegistry.removeSessionInformation(request.getRequestedSessionId());
        securityContextRepository.clearContext(request);
    }

}
