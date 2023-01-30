package org.xgllhz.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.xgllhz.security.config.MySecurityMetadataSource;
import org.xgllhz.security.entity.PermEntity;
import org.xgllhz.security.entity.RoleEntity;
import org.xgllhz.security.entity.RolePermEntity;
import org.xgllhz.security.exception.GlobalException;
import org.xgllhz.security.mapper.RoleMapper;
import org.xgllhz.security.mapper.RolePermMapper;
import org.xgllhz.security.service.IRolePermService;
import org.xgllhz.security.service.IRoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: XGLLHZ
 * @date: 2022/8/30 19:34
 * @description: role service impl
 */
@Service
@Primary
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleEntity> implements IRoleService {

    private static final Log logger = LogFactory.getLog(RoleServiceImpl.class);

    private final RoleMapper roleMapper;

    private final RolePermMapper rolePermMapper;

    private final IRolePermService rolePermService;

    private final MySecurityMetadataSource securityMetadataSource;

    @Override
    public IPage<RoleEntity> listRole(RoleEntity roleEntity) {
        IPage<RoleEntity> page = new Page<>(roleEntity.getCurrent(), roleEntity.getSize());
        try {
            List<RoleEntity> list = this.roleMapper.listRole(page, roleEntity);
            page.setRecords(list);
            return page;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return page;
        }
    }

    @Override
    public RoleEntity getRole(RoleEntity roleEntity) {
        try {
            RoleEntity role = this.checkRole(roleEntity);

            List<PermEntity> list = this.roleMapper.listPermByRoleId(roleEntity);
            if (!CollectionUtils.isEmpty(list)) {
                role.setPermList(list);

                List<Long> permIds = list.stream().map(PermEntity::getId).collect(Collectors.toList());
                role.setPermIds(permIds);
            }

            return role;
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void addRole(RoleEntity roleEntity) {
        try {
            this.checkRoleName(roleEntity);

            this.roleMapper.insert(roleEntity);

            this.addRolePerm(roleEntity);
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteRole(RoleEntity roleEntity) {
        try {
            this.checkRole(roleEntity);

            this.deleteRolePerm(roleEntity);

            this.roleMapper.deleteById(roleEntity.getId());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void updateRole(RoleEntity roleEntity) {
        try {
            this.checkRole(roleEntity);
            this.checkRoleName(roleEntity);

            this.roleMapper.updateById(roleEntity);

            this.deleteRolePerm(roleEntity);
            this.addRolePerm(roleEntity);
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), e);
        }
    }

    /**
     * 检查 role
     * @param roleEntity
     */
    private RoleEntity checkRole(RoleEntity roleEntity) {
        RoleEntity role = this.roleMapper.selectById(roleEntity.getId());
        if (role == null) {
            throw new GlobalException("Role data does not exist!");
        }
        return role;
    }

    /**
     * 检查 role name
     * @param roleEntity
     */
    private void checkRoleName(RoleEntity roleEntity) {
        RoleEntity role = this.roleMapper.selectOne(new QueryWrapper<RoleEntity>()
                .eq("role_namey", roleEntity.getRoleNamey()));
        if (role != null) {
            throw new GlobalException("Role english name cannot be repetitive!");
        }
    }

    /**
     * 新增 role perm
     * @param roleEntity
     */
    private void addRolePerm(RoleEntity roleEntity) {
        if (CollectionUtils.isEmpty(roleEntity.getPermIds())) {
            return;
        }

        List<RolePermEntity> list = new ArrayList<>(roleEntity.getPermIds().size());
        roleEntity.getPermIds().forEach(permId -> list.add(RolePermEntity.builder()
                .roleId(roleEntity.getId())
                .permId(permId)
                .build()));

        this.rolePermService.saveBatch(list);

        this.securityMetadataSource.refreshPermRole();
    }

    /**
     * 删除 role perm
     * @param roleEntity
     */
    private void deleteRolePerm(RoleEntity roleEntity) {
        List<RolePermEntity> list = this.rolePermMapper.selectList(new QueryWrapper<RolePermEntity>()
                .eq("role_id", roleEntity.getId()));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        List<Long> ids = list.stream().map(RolePermEntity::getId).collect(Collectors.toList());
        this.rolePermMapper.deleteBatchIds(ids);

        this.securityMetadataSource.refreshPermRole();
    }

}
