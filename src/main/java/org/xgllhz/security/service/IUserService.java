package org.xgllhz.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.xgllhz.security.entity.PermEntity;
import org.xgllhz.security.entity.TreeEntity;
import org.xgllhz.security.entity.UserEntity;

import java.util.List;

/**
 * @author: XGLLHZ
 * @date: 2022/7/13 23:46
 * @description: user service
 */
public interface IUserService extends UserDetailsService, IService<UserEntity> {

    /**
     * 用户列表
     * @param userEntity
     * @return
     */
    IPage<UserEntity> listUser(UserEntity userEntity);

    /**
     * 用户详情
     * @param userEntity
     * @return
     */
    UserEntity getUser(UserEntity userEntity);

    /**
     * 新增用户
     * @param userEntity
     */
    void addUser(UserEntity userEntity);

    /**
     * 删除用户
     * @param userEntity
     */
    void deleteUser(UserEntity userEntity);

    /**
     * 更新用户
     * @param userEntity
     */
    void updateUser(UserEntity userEntity);

    /**
     * 更新密码
     * @param userEntity
     */
    void updatePassword(UserEntity userEntity);

    /**
     * 根据 userId 获取权限列表
     * @param userEntity
     * @return
     */
    List<TreeEntity> obtainUserPermTree(UserEntity userEntity);

}
