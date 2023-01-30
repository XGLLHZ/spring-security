package org.xgllhz.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.stereotype.Component;

/**
 * @author: XGLLHZ
 * @date: 2022/8/11 21:33
 * @description: FilterSecurityInterceptor post processor
 */
@Component
@RequiredArgsConstructor
public class FilterSecurityInterceptorPostProcessor implements ObjectPostProcessor<FilterSecurityInterceptor> {

    private final AccessDecisionManager accessDecisionManager;

    private final FilterInvocationSecurityMetadataSource securityMetadataSource;

    @Override
    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
        object.setAccessDecisionManager(accessDecisionManager);
        object.setSecurityMetadataSource(securityMetadataSource);
        return object;
    }

}
