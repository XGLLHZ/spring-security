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

        // AuthorizationFilter 授权
        /*httpSecurity.authorizeHttpRequests()
                    .antMatchers("/user/**", "/perm/**", "/role/**")
                    .hasAnyRole("SUPER-ADMIN", "ADMIN")
                    .antMatchers("/web/**")
                    .hasAnyRole("WEB");*/

        // FilterSecurityInterceptor url 授权
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

        // 因为放入 redis 的 SecurityContext 中的 Authentication 接口的相关实现类没有无参构造函数 会导致反序列化失败
        // 所以需要借助 security 中的相关 module
        objectMapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));

        // 由于反序列化经常出现漏洞 故 spring security 增加了反序列化类的白名单 即只有加入此白名单的类才可被反序列化
        // 而放入 redis 中的 SecurityContext 中的 authentication 中的 principal 对象实际上是我们自定义实现的用户类（UserEntity）
        // 该类需要被反序列化 但却没被加入白名单 所以需要将其加入白名单
        // 加入白名单有两种方式 即加入 mixIn 和 使用 jackson 相关注解（以下使用方式一）
        objectMapper.addMixIn(UserEntity.class, UserEntity.UserEntityMixin.class);
        return objectMapper;
    }

    @Bean("objectMapper")
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * 跨域
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
