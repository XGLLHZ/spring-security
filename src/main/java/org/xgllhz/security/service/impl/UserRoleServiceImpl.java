package org.xgllhz.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.xgllhz.security.entity.UserRoleEntity;
import org.xgllhz.security.mapper.UserRoleMapper;
import org.xgllhz.security.service.IUserRoleService;

/**
 * @author: XGLLHZ
 * @date: 2022/8/31 17:26
 * @description: user role service impl
 */
@Service
@Primary
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRoleEntity> implements IUserRoleService {

}
