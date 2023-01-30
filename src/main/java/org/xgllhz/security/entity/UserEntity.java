package org.xgllhz.security.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.MissingNode;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author: XGLLHZ
 * @date: 2022/8/7 22:09
 * @description: sys user entity
 */
@Data
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class UserEntity extends BaseEntity implements UserDetails {

    private static final long serialVersionUID = -3968974790952655396L;

    @NotBlank(message = "User account cannot be empty")
    private String account;   // 账号（用户名）

    private String password;   // 密码（凭证）

    @NotNull(message = "Account type cannot be empty")
    private Integer accountType;   // 账号类型: 0: 超级管理员; 1: 管理员; 2: 业务员; 3: 用户

    private String nickname;   // 昵称

    private Integer accountExpireState;   // 账号过期状态: 0: 未过期; 1: 已过期

    private Integer passwordExpireState;   // 密码过期状态: 0: 未过期; 1: 已过期

    private Integer lockState;   // 账号锁定状态: 0: 未锁定; 1: 已锁定

    private Integer enableState;   // 账号启用状态: 0: 启用; 1: 禁用

    @TableField(exist = false)
    private String oldPassword;   // 旧密码

    @NotEmpty(message = "Role list cannot be empty")
    @TableField(exist = false)
    private List<Long> roleIds;   // 角色 ids

    @TableField(exist = false)
    private List<String> roleNameList;   // 角色英文名列表

    @TableField(exist = false)
    private List<RoleEntity> roleList;   // 角色列表

    @TableField(exist = false)
    private List<TreeEntity> permTree;   // 权限树

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>(this.roleNameList.size());

        this.roleNameList.forEach(role -> authorityList.add(new SimpleGrantedAuthority(role)));

        return authorityList;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.account;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountExpireState == 0;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.lockState == 0;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.passwordExpireState == 0;
    }

    @Override
    public boolean isEnabled() {
        return this.enableState == 0;
    }

    public static class UserEntityDeserializer extends JsonDeserializer<UserEntity> {

        @Override
        public UserEntity deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {

            ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();

            JsonNode jsonNode = mapper.readTree(jsonParser);

            long id = getJsonNode(jsonNode, "id").asLong();
            String account = getJsonNode(jsonNode, "account").asText();
            String password = getJsonNode(jsonNode, "password").asText("");
            int accountType = getJsonNode(jsonNode, "accountType").asInt();
            String nickname = getJsonNode(jsonNode, "nickname").asText();
            int accountExpireState = getJsonNode(jsonNode, "accountExpireState").asInt();
            int passwordExpireState = getJsonNode(jsonNode, "passwordExpireState").asInt();
            int lockState = getJsonNode(jsonNode, "lockState").asInt();
            int enableState = getJsonNode(jsonNode, "enableState").asInt();
            List<String> roleList = mapper.convertValue(getJsonNode(jsonNode, "roleNameList"), new TypeReference<List<String>>() {});

            return UserEntity.builder()
                    .id(id)
                    .account(account)
                    .password(password)
                    .accountType(accountType)
                    .nickname(nickname)
                    .accountExpireState(accountExpireState)
                    .passwordExpireState(passwordExpireState)
                    .lockState(lockState)
                    .enableState(enableState)
                    .roleNameList(roleList)
                    .build();
        }

        private JsonNode getJsonNode(JsonNode jsonNode, String field) {
            return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @JsonDeserialize(using = UserEntityDeserializer.class)
    public abstract static class UserEntityMixin {

    }

}
