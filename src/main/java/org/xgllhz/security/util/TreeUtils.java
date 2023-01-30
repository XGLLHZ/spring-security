package org.xgllhz.security.util;

import org.springframework.util.CollectionUtils;
import org.xgllhz.security.entity.TreeEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: XGLLHZ
 * @date: 2022/8/26 20:24
 * @description: tree util
 */
public class TreeUtils {

    /**
     * 获取指定父节点的一级子节点
     * @param parentId
     * @param list
     * @return
     */
    public static List<? extends TreeEntity> getChildrenNode(long parentId, List<? extends TreeEntity> list) {
        return list.stream().filter(treeEntity -> Objects.equals(parentId, treeEntity.getParentId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取指定父节点的所有子节点
     * @param parentId 指定父 id
     * @param list 树形列表
     * @param childrenList 结果集
     */
    public static void getAllChildrenNode(long parentId, List<? extends TreeEntity> list,
                                          List<TreeEntity> childrenList) {
        list.stream().peek(treeEntity -> {
            if (Objects.equals(parentId, treeEntity.getParentId())) {
                childrenList.add(treeEntity);
                if (!CollectionUtils.isEmpty(treeEntity.getChildrenList())) {
                    TreeUtils.getAllChildrenNode(treeEntity.getId(), treeEntity.getChildrenList(), childrenList);
                }
            }
            treeEntity.setChildrenList(Collections.emptyList());
        }).collect(Collectors.toList());
    }

    /**
     * 构建指定节点的树形结构
     * @param entity
     * @param list
     * @return
     */
    public static TreeEntity buildTree(TreeEntity entity, List<? extends TreeEntity> list) {
        List<TreeEntity> childrenList = new ArrayList<>();
        list.stream().peek(treeEntity -> {
            if (Objects.equals(entity.getId(), treeEntity.getParentId())) {
                childrenList.add(TreeUtils.buildTree(treeEntity, list));
            }
        }).collect(Collectors.toList());
        entity.setChildrenList(childrenList);
        return entity;
    }

}
