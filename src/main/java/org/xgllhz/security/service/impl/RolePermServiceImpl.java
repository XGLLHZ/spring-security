package org.xgllhz.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.xgllhz.security.entity.RolePermEntity;
import org.xgllhz.security.mapper.RolePermMapper;
import org.xgllhz.security.service.IRolePermService;

/**
 * @author: XGLLHZ
 * @date: 2022/8/31 15:15
 * @description: role perm service impl
 */
@Service
@Primary
public class RolePermServiceImpl extends ServiceImpl<RolePermMapper, RolePermEntity> implements IRolePermService {

}
