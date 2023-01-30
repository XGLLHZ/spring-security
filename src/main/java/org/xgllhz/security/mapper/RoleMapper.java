package org.xgllhz.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.xgllhz.security.entity.PermEntity;
import org.xgllhz.security.entity.RoleEntity;

import java.util.List;

/**
 * @author: XGLLHZ
 * @date: 2022/8/26 18:21
 * @description: role entity
 */
public interface RoleMapper extends BaseMapper<RoleEntity> {

    /**
     * 角色列表
     * @param page
     * @param roleEntity
     * @return
     */
    List<RoleEntity> listRole(IPage<RoleEntity> page, @Param("condition") RoleEntity roleEntity);

    /**
     * 根据 roleId 获取权限列表
     * @param roleEntity
     * @return
     */
    List<PermEntity> listPermByRoleId(@Param("condition") RoleEntity roleEntity);

}
