package org.xgllhz.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.xgllhz.security.entity.PermEntity;
import org.xgllhz.security.entity.RoleEntity;
import org.xgllhz.security.mapper.PermMapper;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author: XGLLHZ
 * @date: 2022/8/8 21:41
 * @description: custom resource-permission metadata source
 */
@Component
@RequiredArgsConstructor
public class MySecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final Map<String, List<String>> requestMap = new HashMap<>();

    private final PermMapper permMapper;

    @PostConstruct
    public void init() {
        this.refreshPermRole();
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {

        String requestUrl = ((FilterInvocation) object).getRequestUrl();

        for (Map.Entry<String, List<String>> entry : this.requestMap.entrySet()) {
            if (this.antPathMatcher.match(entry.getKey(), requestUrl)) {
                return SecurityConfig.createList(entry.getValue().toArray(new String[0]));
            }
        }

        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> configAttributes = new HashSet<>();
        this.requestMap.values().forEach(list -> configAttributes.addAll(SecurityConfig.createList(list.toArray(new String[0]))));
        return configAttributes;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    /**
     * 刷新权限-角色列表映射
     */
    public void refreshPermRole() {
        List<PermEntity> allPermRole = permMapper.allPermRole();

        for (PermEntity perm : allPermRole) {
            if (ObjectUtils.isEmpty(perm.getPermUrl())) {
                continue;
            }
            List<String> roles;
            if (this.requestMap.containsKey(perm.getPermUrl())) {
                roles = this.requestMap.get(perm.getPermUrl());
            } else {
                roles = new ArrayList<>();
            }
            for (RoleEntity role : perm.getRoleList()) {
                roles.add(role.getRoleNamey());
            }
            this.requestMap.put(perm.getPermUrl(), roles);
        }
    }

}
