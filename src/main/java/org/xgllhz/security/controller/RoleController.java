package org.xgllhz.security.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xgllhz.security.entity.RoleEntity;
import org.xgllhz.security.service.IRoleService;
import org.xgllhz.security.util.APIResponse;

/**
 * @author: XGLLHZ
 * @date: 2022/8/31 16:11
 * @description: role controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/role")
public class RoleController {

    private static final Log logger = LogFactory.getLog(RoleController.class);

    private final IRoleService roleService;

    @PostMapping("/listRole")
    public APIResponse<IPage<RoleEntity>> listRole(@RequestBody RoleEntity roleEntity) {
        logger.info("enter RoleController.listRole() params = " + roleEntity);
        return APIResponse.success(this.roleService.listRole(roleEntity));
    }

    @PostMapping("/getRole")
    public APIResponse<RoleEntity> getRole(@RequestBody RoleEntity roleEntity) {
        logger.info("enter RoleController.getRole() params = " + roleEntity);
        return APIResponse.success(this.roleService.getRole(roleEntity));
    }

    @PostMapping("/addRole")
    public APIResponse<Object> addRole(@RequestBody RoleEntity roleEntity) {
        logger.info("enter RoleController.addRole() params = " + roleEntity);
        this.roleService.addRole(roleEntity);
        return APIResponse.success();
    }

    @PostMapping("/deleteRole")
    public APIResponse<Object> deleteRole(@RequestBody RoleEntity roleEntity) {
        logger.info("enter RoleController.deleteRole() params = " + roleEntity);
        this.roleService.deleteRole(roleEntity);
        return APIResponse.success();
    }

    @PostMapping("/updateRole")
    public APIResponse<Object> updateRole(@RequestBody RoleEntity roleEntity) {
        logger.info("enter RoleController.updateRole() params = " + roleEntity);
        this.roleService.updateRole(roleEntity);
        return APIResponse.success();
    }

}
