package org.xgllhz.security.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author: XGLLHZ
 * @date: 2022/8/26 18:19
 * @description: role entity
 */
@Data
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class RoleEntity extends BaseEntity {

    private static final long serialVersionUID = -5945911774569902255L;

    @NotBlank(message = "Role english name cannot be empty")
    private String roleNamey;   // 英文名

    @NotBlank(message = "Role chinese name cannot be empty")
    private String roleNamez;   // 中文名

    @NotEmpty(message = "Permission list cannot be empty")
    @TableField(exist = false)
    private List<Long> permIds;   // 权限 ids

    @TableField(exist = false)
    private List<PermEntity> permList;   // 权限列表

}
