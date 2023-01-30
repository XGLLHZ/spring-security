package org.xgllhz.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.xgllhz.security.util.RedisUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: XGLLHZ
 * @date: 2022/8/13 16:45
 * @description: custom session registry
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MySessionRegistry implements SessionRegistry {

    // hash   hashKey: Principal   value: sessionIds
    private static final String PRINCIPAL_SESSIONS_PREFIX = "security:session:principal";

    // hash   hashKey: sessionId   value: SessionInformation
    public static final String SESSION_INFORMATION_PREFIX = "security:session:information";

    private static RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "objectMapper")
    private final ObjectMapper objectMapper;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        MySessionRegistry.redisTemplate = redisTemplate;
    }

    @Override
    public List<Object> getAllPrincipals() {
        return new ArrayList<>(RedisUtils.getHashKeys(redisTemplate, PRINCIPAL_SESSIONS_PREFIX));
    }

    @Override
    public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
        if (principal == null) {
            return new ArrayList<>();
        }

        Set<String> sessionIds = getSessionIds(principal);
        if (CollectionUtils.isEmpty(sessionIds)) {
            return new ArrayList<>();
        }

        List<SessionInformation> list = new ArrayList<>();
        for (String sessionId : sessionIds) {
            SessionInformation sessionInformation = getSessionInformation(sessionId);
            if (sessionInformation == null) {
                continue;
            }
            if (includeExpiredSessions || !sessionInformation.isExpired()) {
                list.add(sessionInformation);
            }
        }
        return list;
    }

    @Override
    public SessionInformation getSessionInformation(String sessionId) {
        return getSession(sessionId);
    }

    @Override
    public void refreshLastRequest(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");

        SessionInformation sessionInformation = getSessionInformation(sessionId);
        if (sessionInformation != null) {
            sessionInformation.refreshLastRequest();
            RedisUtils.addHashValue(redisTemplate, SESSION_INFORMATION_PREFIX, sessionId, sessionInformation,
                    YmlConfig.getRedisExpirationTimeout(), TimeUnit.SECONDS);
        }
    }

    @Override
    public void registerNewSession(String sessionId, Object principal) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");
        Assert.notNull(principal, "Principal required as per interface contract");

        if (getSessionInformation(sessionId) != null) {
            removeSessionInformation(sessionId);
        }

        Set<String> sessionIds = getSessionIds(principal);
        sessionIds.add(sessionId);

        RedisUtils.addHashValue(redisTemplate, PRINCIPAL_SESSIONS_PREFIX, principal, sessionIds,
                YmlConfig.getRedisExpirationTimeout(), TimeUnit.SECONDS);
        RedisUtils.addHashValue(redisTemplate, SESSION_INFORMATION_PREFIX, sessionId, new MySessionInformation(principal, sessionId, new Date(), false),
                YmlConfig.getRedisExpirationTimeout(), TimeUnit.SECONDS);
    }

    @Override
    public void removeSessionInformation(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");

        SessionInformation sessionInformation = getSessionInformation(sessionId);
        if (sessionInformation == null) {
            return;
        }

        RedisUtils.deleteHashKey(redisTemplate, SESSION_INFORMATION_PREFIX, sessionId);
        if (sessionInformation.getPrincipal() == null) {
            return;
        }

        Set<String> sessionIds = getSessionIds(sessionInformation.getPrincipal());
        if (CollectionUtils.isEmpty(sessionIds)) {
            return;
        }

        sessionIds.remove(sessionId);
        RedisUtils.addHashValue(redisTemplate, PRINCIPAL_SESSIONS_PREFIX, sessionInformation.getPrincipal(), sessionIds,
                YmlConfig.getRedisExpirationTimeout(), TimeUnit.SECONDS);
    }

    /**
     * 根据 principal 获取 sessionIds
     * @param principal
     * @return
     */
    private Set<String> getSessionIds(Object principal) {
        Object o = RedisUtils.getHashValue(redisTemplate, PRINCIPAL_SESSIONS_PREFIX, principal);
        if (o == null) {
            return new HashSet<>();
        } else {
            return new HashSet<>((List<String>) o);
        }
    }

    /**
     * 根据 sessionId 获取 session information
     * @param sessionId
     * @return
     */
    public static MySessionInformation getSession(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");

        try {
            Object o = RedisUtils.getHashValue(redisTemplate, SESSION_INFORMATION_PREFIX, sessionId);
            if (o == null) {
                return null;
            }

            Map<String, Object> map = (Map<String, Object>) o;
            Date lastRequest = new Date((Long) map.get("lastRequest"));
            Object principal = map.get("principal");
            boolean expireState = (boolean) map.get("expireState");

            return new MySessionInformation(principal, sessionId, lastRequest, expireState);
        } catch (Exception e) {
            log.error("Failed to convert Map to Object!", e);
            return null;
        }
    }

    /**
     * 更新 session state
     * @param sessionInformation
     */
    public static void updateSessionState(SessionInformation sessionInformation) {
        RedisUtils.addHashValue(redisTemplate, SESSION_INFORMATION_PREFIX, sessionInformation.getSessionId(), sessionInformation,
                YmlConfig.getRedisExpirationTimeout(), TimeUnit.SECONDS);
    }

}
