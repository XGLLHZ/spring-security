package org.xgllhz.security.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author: XGLLHZ
 * @date: 2022/8/9 21:28
 * @description: custom access decision manager
 */
@Component
public class MyAccessDecisionManager implements AccessDecisionManager {

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {

        Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();
        Iterator<ConfigAttribute> iterator = configAttributes.iterator();

        String requestRole;
        while (iterator.hasNext()) {

            requestRole = iterator.next().getAttribute();
            if (StringUtils.equals("LOGIN_ROLE", requestRole)) {
                return;
            }

            for (GrantedAuthority grantedAuthority : grantedAuthorities) {
                if (StringUtils.equals(requestRole, grantedAuthority.getAuthority())) {
                    return;
                }
            }
        }

        throw new AccessDeniedException("无权访问！");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }
}
