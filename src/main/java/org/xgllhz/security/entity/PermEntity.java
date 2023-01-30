package org.xgllhz.security.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: XGLLHZ
 * @date: 2022/8/26 17:20
 * @description: sys perm entity
 */
@Data
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_perm")
public class PermEntity extends TreeEntity {

    private static final long serialVersionUID = -75209805692188644L;

    @NotBlank(message = "Permission name cannot be empty")
    private String permName;   // 权限名称

    private String permUrl;   // 权限 url

    private String routerComponent;   // 路由组件

    private String routerPath;   // 路由 path

    private String routerIcon;   // 路由 icon

    private Integer routerHidden;   // 路由 hidden 值: 0: 显示; 1: 隐藏

    @NotNull(message = "Permission type cannot be empty")
    private Integer permType;   // 权限类型: 0: 菜单; 1: 子菜单; 2: 操作

    private Integer permSort;   // 权限排序

    @TableField(exist = false)
    private String parentName;   // 父权限名称

    @TableField(exist = false)
    private List<RoleEntity> roleList;   // 权限-角色 列表

}
