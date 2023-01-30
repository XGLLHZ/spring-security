package org.xgllhz.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author: XGLLHZ
 * @date: 2022/8/26 19:21
 * @description: user role entity
 */
@Data
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_role")
public class UserRoleEntity extends BaseEntity {

    private static final long serialVersionUID = -6079787443907053664L;

    private Long userId;   // 用户id

    private Long roleId;   // 角色id

}
