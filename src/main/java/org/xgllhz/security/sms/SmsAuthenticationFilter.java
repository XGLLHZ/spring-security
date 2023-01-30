package org.xgllhz.security.sms;

import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: guo hao
 * @date: 2022/7/15 9:36
 * @description: sms authentication filter
 */
public class SmsAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public SmsAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login", HttpMethod.POST.name()));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        String mobilePhone = request.getParameter("mobilePhone");
        String smsCode = request.getParameter("smsCode");

        SmsAuthenticationToken smsAuthenticationToken = new SmsAuthenticationToken(mobilePhone, smsCode);

        smsAuthenticationToken.setDetails(this.authenticationDetailsSource.buildDetails(request));

        return this.getAuthenticationManager().authenticate(smsAuthenticationToken);
    }

}
