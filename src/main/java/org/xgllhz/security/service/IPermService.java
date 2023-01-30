package org.xgllhz.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.xgllhz.security.entity.PermEntity;
import org.xgllhz.security.entity.TreeEntity;

import java.util.List;

/**
 * @author: XGLLHZ
 * @date: 2022/8/26 18:00
 * @description: perm service
 */
public interface IPermService extends IService<PermEntity> {

    /**
     * 权限列表
     * @param permEntity
     * @return
     */
    IPage<PermEntity> listPerm(PermEntity permEntity);

    /**
     * 权限详情
     * @param permEntity
     * @return
     */
    PermEntity getPerm(PermEntity permEntity);

    /**
     * 新增权限
     * @param permEntity
     */
    void addPerm(PermEntity permEntity);

    /**
     * 删除权限
     * @param permEntity
     */
    void deletePerm(PermEntity permEntity);

    /**
     * 更新权限
     * @param permEntity
     */
    void updatePerm(PermEntity permEntity);

    /**
     * 权限树
     * @param permEntity
     * @return
     */
    List<TreeEntity> getPermTree(PermEntity permEntity);

    /**
     * 子权限
     * @param permEntity
     * @return
     */
    List<TreeEntity> getChildrenList(PermEntity permEntity);

}
