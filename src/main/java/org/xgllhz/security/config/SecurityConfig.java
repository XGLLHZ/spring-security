package org.xgllhz.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.UrlAuthorizationConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.xgllhz.security.entity.UserEntity;
import org.xgllhz.security.sms.SmsAuthenticationConfigurer;

/**
 * @author: XGLLHZ
 * @date: 2022/7/10 22:44
 * @description: security config
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   UserDetailsService userDetailsService,
                                                   AuthenticationSuccessHandler authenticationSuccessHandler,
                                                   AuthenticationFailureHandler authenticationFailureHandler,
                                                   AuthenticationEntryPoint authenticationEntryPoint,
                                                   SessionRegistry sessionRegistry,
                                                   SessionInformationExpiredStrategy sessionExpiredHandler,
                                                   SecurityContextRepository securityContextRepository,
                                                   AccessDeniedHandler accessDeniedHandler,
                                                   SmsAuthenticationConfigurer<HttpSecurity> smsAuthenticationConfigurer,
                                                   ObjectPostProcessor<FilterSecurityInterceptor> objectPostProcessor,
                                                   LogoutHandler logoutHandler,
                                                   LogoutSuccessHandler logoutSuccessHandler) throws Exception {

        // csrf
        httpSecurity.csrf()
                .disable();

        // cors
        httpSecurity.cors()
                .configurationSource(configurationSource());

        // user details service
        httpSecurity.userDetailsService(userDetailsService);

        // username password authentication
        httpSecurity.formLogin()
                .loginProcessingUrl("/admin/user/login")
                .usernameParameter("account")
                .passwordParameter("password")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler);

        // sms code authentication
        httpSecurity.apply(smsAuthenticationConfigurer)
                .loginProcessingUrl("/admin/sms/login")
                .setSuccessHandler(authenticationSuccessHandler)
                .setFailureHandler(authenticationFailureHandler);

        // session manager
        httpSecurity.sessionManagement()
                .maximumSessions(2)
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(sessionRegistry)
                .expiredSessionStrategy(sessionExpiredHandler);

        // security context repository
        httpSecurity.securityContext()
                .requireExplicitSave(true)
                .securityContextRepository(securityContextRepository);

        // authentication entry point
        httpSecurity.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint);

        // FilterSecurityInterceptor expression authorization
        httpSecurity.authorizeRequests()
                .withObjectPostProcessor(objectPostProcessor)
                .antMatchers("/admin/user/login", "/admin/sms/login")
                .permitAll();

        // authorize exception handler
        httpSecurity.exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler);

        // logout
        httpSecurity.logout()
                .logoutUrl("/admin/user/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(logoutSuccessHandler);

        // AuthorizationFilter ??????
        /*httpSecurity.authorizeHttpRequests()
                    .antMatchers("/user/**", "/perm/**", "/role/**")
                    .hasAnyRole("SUPER-ADMIN", "ADMIN")
                    .antMatchers("/web/**")
                    .hasAnyRole("WEB");*/

        // FilterSecurityInterceptor url ??????
        /*httpSecurity.apply(new UrlAuthorizationConfigurer<>(httpSecurity.getSharedObject(ApplicationContext.class)))
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setSecurityMetadataSource(securityMetadataSource);
                        return object;
                    }
                })
                .getRegistry()
                .antMatchers("/admin/user/login", "/admin/sms/login")
                        .hasAnyRole("LOGIN_ROLE");*/

        return httpSecurity.build();
    }

    @Bean("springSecurityObjectMapper")
    public ObjectMapper springSecurityObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // ???????????? redis ??? SecurityContext ?????? Authentication ???????????????????????????????????????????????? ???????????????????????????
        // ?????????????????? security ???????????? module
        objectMapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));

        // ???????????????????????????????????? ??? spring security ???????????????????????????????????? ??????????????????????????????????????????????????????
        // ????????? redis ?????? SecurityContext ?????? authentication ?????? principal ??????????????????????????????????????????????????????UserEntity???
        // ??????????????????????????? ??????????????????????????? ?????????????????????????????????
        // ?????????????????????????????? ????????? mixIn ??? ?????? jackson ???????????????????????????????????????
        objectMapper.addMixIn(UserEntity.class, UserEntity.UserEntityMixin.class);
        return objectMapper;
    }

    @Bean("objectMapper")
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * ??????
     * @return
     */
    private CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }

}
