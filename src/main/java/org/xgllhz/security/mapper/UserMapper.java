package org.xgllhz.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.xgllhz.security.entity.PermEntity;
import org.xgllhz.security.entity.RoleEntity;
import org.xgllhz.security.entity.UserEntity;

import java.util.List;

/**
 * @author: XGLLHZ
 * @date: 2022/8/26 11:20
 * @description: user mapper
 */
public interface UserMapper extends BaseMapper<UserEntity> {

    /**
     * 用户列表
     * @param page
     * @param userEntity
     * @return
     */
    List<UserEntity> listUser(Page<UserEntity> page, @Param("condition") UserEntity userEntity);

    /**
     * 根据 userId 互获取角色列表
     * @param userEntity
     * @return
     */
    List<RoleEntity> listRoleByUserId(@Param("condition") UserEntity userEntity);

    /**
     * 根据 userId 获取权限列表
     * @param userEntity
     * @return
     */
    List<PermEntity> listPermByUserId(@Param("condition") UserEntity userEntity);

}
