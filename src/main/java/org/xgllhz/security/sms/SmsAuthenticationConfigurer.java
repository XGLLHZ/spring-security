package org.xgllhz.security.sms;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

/**
 * @author: guo hao
 * @date: 2022/7/15 9:56
 * @description: sms authentication configurer
 */
@Component
public class SmsAuthenticationConfigurer<B extends HttpSecurityBuilder<B>>
        extends AbstractAuthenticationFilterConfigurer<B, SmsAuthenticationConfigurer<B>, SmsAuthenticationFilter> {

    private AuthenticationSuccessHandler successHandler;

    private AuthenticationFailureHandler failureHandler;

    public SmsAuthenticationConfigurer() {
        super(new SmsAuthenticationFilter(), null);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl);
    }

    @Override
    public void configure(B http) throws Exception {
        SmsAuthenticationFilter smsAuthenticationFilter = this.getAuthenticationFilter();

        smsAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));

        smsAuthenticationFilter.setAuthenticationSuccessHandler(successHandler);
        smsAuthenticationFilter.setAuthenticationFailureHandler(failureHandler);

        SmsAuthenticationProvider smsAuthenticationProvider = new SmsAuthenticationProvider();

        http.authenticationProvider(smsAuthenticationProvider)
                .addFilterAfter(smsAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    public SmsAuthenticationConfigurer<B> setSuccessHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    public SmsAuthenticationConfigurer<B> setFailureHandler(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
        return this;
    }

}
