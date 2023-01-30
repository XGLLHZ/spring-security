package org.xgllhz.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author: XGLLHZ
 * @date: 2022/8/26 19:20
 * @description: role perm entity
 */
@Data
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_perm")
public class RolePermEntity extends BaseEntity {

    private static final long serialVersionUID = -1624222146690649328L;

    private Long roleId;   // 角色id

    private Long permId;   // 权限id

}
