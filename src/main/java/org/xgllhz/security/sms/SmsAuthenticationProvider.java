package org.xgllhz.security.sms;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Collections;

/**
 * @author: guo hao
 * @date: 2022/7/15 9:40
 * @description: sms authentication provider
 */
public class SmsAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        /**
         * 1、从 redis 中根据 mobilePhone 获取到实际生成的 smsCode
         * 2、从 authentication 中取出前端传过来的 smsCode
         * 3、检查是否相等
         */

        String smsCode = "123456";

        String credentials = (String) authentication.getCredentials();

        if (StringUtils.equals(smsCode, credentials)) {
            return new SmsAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), Collections.emptyList());
        }

        throw new BadCredentialsException("验证码错误！");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
