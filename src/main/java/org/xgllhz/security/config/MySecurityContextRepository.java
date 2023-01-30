package org.xgllhz.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.xgllhz.security.entity.UserEntity;
import org.xgllhz.security.util.RedisUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author: XGLLHZ
 * @date: 2022/8/11 21:49
 * @description: custom security context repository
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MySecurityContextRepository implements SecurityContextRepository {

    // string   key: account   value: context(SecurityContext)
    private static final String ACCOUNT_CONTEXT_PREFIX = "security:account:context:";

    // hash   hashKey: session   value: account
    private static final String ACCOUNT_SESSIONS_PREFIX = "security:account:sessions";

    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    @Resource(name = "springSecurityRedisTemplate")
    private final RedisTemplate<String, Object> springSecurityRedisTemplate;

    @Resource(name = "springSecurityObjectMapper")
    private final ObjectMapper springSecurityObjectMapper;

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        return getSecurityContext(requestResponseHolder.getRequest());
    }

    @Override
    public Supplier<SecurityContext> loadContext(HttpServletRequest request) {
        return () -> getSecurityContext(request);
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = context.getAuthentication();
        if (authentication == null || this.trustResolver.isAnonymous(authentication)) {
            return;
        }

        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        String account = userEntity.getUsername();

        String session = request.getSession(false).getId();
        if (session == null) {
            return;
        }

        RedisUtils.addValue(springSecurityRedisTemplate, ACCOUNT_CONTEXT_PREFIX + account, context,
                YmlConfig.getRedisExpirationTimeout(), TimeUnit.SECONDS);
        RedisUtils.addHashValue(springSecurityRedisTemplate, ACCOUNT_SESSIONS_PREFIX, session, account,
                YmlConfig.getRedisExpirationTimeout(), TimeUnit.SECONDS);
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        String session = request.getRequestedSessionId();
        if (session == null) {
            return false;
        }
        return RedisUtils.containHashKey(springSecurityRedisTemplate, ACCOUNT_SESSIONS_PREFIX, session);
    }

    /**
     * 清除 context
     * @param request
     */
    public void clearContext(HttpServletRequest request) {
        String session = request.getRequestedSessionId();
        if (session == null) {
            return;
        }

        if (RedisUtils.containHashKey(springSecurityRedisTemplate, ACCOUNT_SESSIONS_PREFIX, session)) {
            RedisUtils.deleteHashKey(springSecurityRedisTemplate, ACCOUNT_SESSIONS_PREFIX, session);
        }
    }

    /**
     * 获取 security context
     * @param request
     * @return
     */
    private SecurityContext getSecurityContext(HttpServletRequest request) {
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();

        String session = request.getRequestedSessionId();
        if (session == null) {
            return emptyContext;
        }

        Object o = RedisUtils.getHashValue(springSecurityRedisTemplate, ACCOUNT_SESSIONS_PREFIX, session);
        if (o == null) {
            return emptyContext;
        }
        try {
            Object result = RedisUtils.getValue(springSecurityRedisTemplate, ACCOUNT_CONTEXT_PREFIX + o);
            SecurityContext securityContext = springSecurityObjectMapper.convertValue(result, SecurityContext.class);
            return securityContext == null ? emptyContext : securityContext;
        } catch (Exception e) {
            log.error("Failed to convert Map to Object!", e);
            return emptyContext;
        }
    }

}
