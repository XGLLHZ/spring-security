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
import org.springframework.util.CollectionUtils;
import org.xgllhz.security.entity.PermEntity;
import org.xgllhz.security.entity.TreeEntity;
import org.xgllhz.security.exception.GlobalException;
import org.xgllhz.security.mapper.PermMapper;
import org.xgllhz.security.service.IPermService;
import org.xgllhz.security.util.TreeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: XGLLHZ
 * @date: 2022/8/26 18:09
 * @description: perm service impl
 */
@Service
@Primary
@RequiredArgsConstructor
public class PermServiceImpl extends ServiceImpl<PermMapper, PermEntity> implements IPermService {

    private static final Log logger = LogFactory.getLog(PermServiceImpl.class);

    private final PermMapper permMapper;

    @Override
    public IPage<PermEntity> listPerm(PermEntity permEntity) {
        IPage<PermEntity> page = new Page<>(permEntity.getCurrent(), permEntity.getSize());
        try {
            List<PermEntity> list = this.permMapper.listPerm(page, permEntity);
            page.setRecords(list);
            return page;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return page;
        }
    }

    @Override
    public PermEntity getPerm(PermEntity permEntity) {
        try {
            PermEntity perm = this.checkPerm(permEntity);

            if (perm.getParentId() != null && perm.getParentId() != 0) {
                PermEntity parentPerm = this.permMapper.selectById(perm.getParentId());
                if (parentPerm != null) {
                    perm.setPermName(parentPerm.getPermName());
                }
            }

            perm.setChildrenList(this.obtainPermTree(PermEntity.builder()
                    .parentId(perm.getId())
                    .build()));

            return perm;
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), e);
        }
    }

    @Override
    public void addPerm(PermEntity permEntity) {
        try {
            this.checkPermName(permEntity);

            if (permEntity.getParentId() != null && permEntity.getParentId() != 0) {
                this.checkPerm(PermEntity.builder()
                        .id(permEntity.getParentId())
                        .build());
            }

            this.permMapper.insert(permEntity);
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), e);
        }
    }

    @Override
    public void deletePerm(PermEntity permEntity) {
        try {
            this.checkPerm(permEntity);

            List<PermEntity> childrenList = this.permMapper.selectList(new QueryWrapper<PermEntity>()
                    .eq("parent_id", permEntity.getId()));
            if (!CollectionUtils.isEmpty(childrenList)) {
                throw new GlobalException("This item has children and cannot be deleted!");
            }

            this.permMapper.deleteById(permEntity.getId());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), e);
        }
    }

    @Override
    public void updatePerm(PermEntity permEntity) {
        try {
            this.checkPerm(permEntity);

            if (permEntity.getParentId() != null && permEntity.getParentId() != 0) {
                this.checkPerm(PermEntity.builder()
                        .id(permEntity.getParentId())
                        .build());
            }

            this.checkPermName(permEntity);

            this.permMapper.updateById(permEntity);
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), e);
        }
    }

    @Override
    public List<TreeEntity> getPermTree(PermEntity permEntity) {
        return this.obtainPermTree(permEntity);
    }

    @Override
    public List<TreeEntity> getChildrenList(PermEntity permEntity) {
        try {
            this.checkPerm(permEntity);
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), e);
        }

        permEntity.setParentId(permEntity.getId());
        List<TreeEntity> permTree = this.obtainPermTree(permEntity);

        List<TreeEntity> childrenList = new ArrayList<>();
        TreeUtils.getAllChildrenNode(permEntity.getId(), permTree, childrenList);

        return childrenList;
    }

    /**
     * 检查 perm
     * @param permEntity
     */
    private PermEntity checkPerm(PermEntity permEntity) {
        PermEntity perm = this.permMapper.selectById(permEntity.getId());
        if (perm == null) {
            throw new GlobalException("Perm data does not exist!");
        }
        return perm;
    }

    /**
     * 检查 perm name
     * @param permEntity
     */
    private void checkPermName(PermEntity permEntity) {
        PermEntity perm = this.permMapper.selectOne(new QueryWrapper<PermEntity>()
                .eq("perm_name", permEntity.getPermName()));
        if (perm != null) {
            throw new GlobalException("Perm name cannot be repetitive!");
        }
    }

    /**
     * 获取权限树
     * @param permEntity
     * @return
     */
    private List<TreeEntity> obtainPermTree(PermEntity permEntity) {
        permEntity.setCurrent(-1);
        permEntity.setSize(-1);

        IPage<PermEntity> list = listPerm(permEntity);
        List<PermEntity> entityList = list.getRecords();

        List<TreeEntity> permTree = new ArrayList<>();
        TreeUtils.getChildrenNode(permEntity.getParentId(), entityList).forEach(item -> {
            TreeUtils.buildTree(item, entityList);
            permTree.add(item);
        });

        return permTree;
    }

}
