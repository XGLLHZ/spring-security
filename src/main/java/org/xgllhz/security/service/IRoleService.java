package org.xgllhz.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.xgllhz.security.entity.RoleEntity;

/**
 * @author: XGLLHZ
 * @date: 2022/8/30 19:30
 * @description: role service
 */
public interface IRoleService extends IService<RoleEntity> {

    /**
     * 角色列表
     * @param roleEntity
     * @return
     */
    IPage<RoleEntity> listRole(RoleEntity roleEntity);

    /**
     * 角色详情
     * @param roleEntity
     * @return
     */
    RoleEntity getRole(RoleEntity roleEntity);

    /**
     * 新增角色
     * @param roleEntity
     */
    void addRole(RoleEntity roleEntity);

    /**
     * 删除角色
     * @param roleEntity
     */
    void deleteRole(RoleEntity roleEntity);

    /**
     * 更新角色
     * @param roleEntity
     */
    void updateRole(RoleEntity roleEntity);

}
