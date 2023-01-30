package org.xgllhz.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.xgllhz.security.entity.PermEntity;

import java.util.List;

/**
 * @author: XGLLHZ
 * @date: 2022/8/26 17:30
 * @description: perm mapper
 */
public interface PermMapper extends BaseMapper<PermEntity> {

    /**
     * 权限列表
     * @param page
     * @param permEntity
     * @return
     */
    List<PermEntity> listPerm(IPage<PermEntity> page, @Param("condition") PermEntity permEntity);

    /**
     * 权限-角色列表 映射
     * @return
     */
    List<PermEntity> allPermRole();

}
