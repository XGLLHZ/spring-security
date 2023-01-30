package org.xgllhz.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.xgllhz.security.entity.*;
import org.xgllhz.security.exception.GlobalException;
import org.xgllhz.security.mapper.UserMapper;
import org.xgllhz.security.mapper.UserRoleMapper;
import org.xgllhz.security.service.IUserRoleService;
import org.xgllhz.security.service.IUserService;
import org.xgllhz.security.util.TreeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: XGLLHZ
 * @date: 2022/7/13 23:47
 * @description: user service impl
 */
@Service
@Primary
@Component
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {

    private static final Log logger = LogFactory.getLog(UserServiceImpl.class);

    private static final String PASSWORD_ID = "{bcrypt}";

    private final UserMapper userMapper;

    private final UserRoleMapper userRoleMapper;

    private final IUserRoleService userRoleService;

    @Override
    public IPage<UserEntity> listUser(UserEntity userEntity) {
        Page<UserEntity> page = new Page<>(userEntity.getCurrent(), userEntity.getSize());
        try {
            List<UserEntity> list = this.userMapper.listUser(page, userEntity);

            for (UserEntity entity : list) {
                entity.setPassword(null);
            }

            page.setRecords(list);
            return page;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return page;
        }
    }

    @Override
    public UserEntity getUser(UserEntity userEntity) {
        try {
            UserEntity user = this.checkUser(userEntity);
            user.setPassword(null);

            this.fillRole(user);

            return user;
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void addUser(UserEntity userEntity) {
        try {
            this.checkUserName(userEntity);

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            userEntity.setPassword(encoder.encode(userEntity.getPassword()));
            this.userMapper.insert(userEntity);

            this.addUserRole(userEntity);
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteUser(UserEntity userEntity) {
        try {
            this.checkUser(userEntity);

            this.deleteUserRole(userEntity);

            this.userMapper.deleteById(userEntity.getId());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void updateUser(UserEntity userEntity) {
        try {
            this.checkUser(userEntity);
            this.checkUserName(userEntity);

            this.userMapper.updateById(userEntity);

            this.deleteUserRole(userEntity);
            this.addUserRole(userEntity);
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), e);
        }
    }

    @Override
    public void updatePassword(UserEntity userEntity) {
        try {
            UserEntity user = this.checkUser(userEntity);

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (!encoder.matches(userEntity.getOldPassword(), user.getPassword())) {
                throw new GlobalException("The old password error!");
            }

            String password = encoder.encode(userEntity.getPassword());
            if (encoder.matches(userEntity.getOldPassword(), password)) {
                throw new GlobalException("Old and new passwords cannot be the same!");
            }

            userEntity.setPassword(password);
            this.userMapper.updateById(userEntity);
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), e);
        }
    }

    @Override
    public List<TreeEntity> obtainUserPermTree(UserEntity userEntity) {
        try {
            List<PermEntity> list = this.userMapper.listPermByUserId(userEntity);

            List<TreeEntity> permTree = new ArrayList<>();
            TreeUtils.getChildrenNode(0L, list).forEach(item -> {
                TreeUtils.buildTree(item, list);
                permTree.add(item);
            });

            return permTree;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = this.userMapper.selectOne(new QueryWrapper<UserEntity>()
                .eq("account", username));
        if (user == null) {
            throw new GlobalException("Username does not exist!");
        }

        user.setPassword(PASSWORD_ID + user.getPassword());

        this.fillRole(user);

        return user;
    }

    /**
     * 检查 user
     * @param userEntity
     * @return
     */
    private UserEntity checkUser(UserEntity userEntity) {
        UserEntity user = this.userMapper.selectById(userEntity.getId());
        if (user == null) {
            throw new GlobalException("User data does not exist!");
        }
        return user;
    }

    /**
     * 检查 account nickname
     * @param userEntity
     */
    private void checkUserName(UserEntity userEntity) {
        List<UserEntity> list = this.userMapper.selectList(new QueryWrapper<UserEntity>()
                .eq("account", userEntity.getAccount())
                .or()
                .eq("nickname", userEntity.getNickname()));
        if (!CollectionUtils.isEmpty(list)) {
            throw new GlobalException("User account or nickname cannot be repetitive!");
        }
    }

    /**
     * 新增 user role
     * @param userEntity
     */
    private void addUserRole(UserEntity userEntity) {
        if (CollectionUtils.isEmpty(userEntity.getRoleIds())) {
            return;
        }

        List<UserRoleEntity> list = new ArrayList<>(userEntity.getRoleIds().size());
        userEntity.getRoleIds().forEach(roleId -> list.add(UserRoleEntity.builder()
                .userId(userEntity.getId())
                .roleId(roleId)
                .build()));

        this.userRoleService.saveBatch(list);
    }

    /**
     * 删除 user role
     * @param userEntity
     */
    private void deleteUserRole(UserEntity userEntity) {
        List<UserRoleEntity> list = this.userRoleMapper.selectList(new QueryWrapper<UserRoleEntity>()
                .eq("user_id", userEntity.getId()));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        List<Long> ids = list.stream().map(UserRoleEntity::getId).collect(Collectors.toList());
        this.userRoleMapper.deleteBatchIds(ids);
    }

    /**
     * 填充角色
     * @param user
     */
    private void fillRole(UserEntity user) {
        List<RoleEntity> list = this.userMapper.listRoleByUserId(user);
        if (!CollectionUtils.isEmpty(list)) {
            user.setRoleList(list);

            List<Long> ids = list.stream().map(RoleEntity::getId).collect(Collectors.toList());
            List<String> roleList = list.stream().map(RoleEntity::getRoleNamey).collect(Collectors.toList());

            user.setRoleIds(ids);
            user.setRoleNameList(roleList);
        }
    }

}
