package org.xgllhz.security.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xgllhz.security.entity.PermEntity;
import org.xgllhz.security.entity.TreeEntity;
import org.xgllhz.security.service.IPermService;
import org.xgllhz.security.util.APIResponse;

import java.util.List;

/**
 * @author: XGLLHZ
 * @date: 2022/8/29 9:45
 * @description: perm controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/perm")
public class PermController {

    private static final Log logger = LogFactory.getLog(PermController.class);

    private final IPermService permService;

    @PostMapping("/listPerm")
    public APIResponse<IPage<PermEntity>> listPerm(@RequestBody PermEntity permEntity) {
        logger.info("enter PermController.listPerm() params = " + permEntity);
        return APIResponse.success(this.permService.listPerm(permEntity));
    }

    @PostMapping("/getPerm")
    public APIResponse<PermEntity> getPerm(@RequestBody PermEntity permEntity) {
        logger.info("enter PermController.getPerm() params = " + permEntity);
        return APIResponse.success(this.permService.getPerm(permEntity));
    }

    @PostMapping("/addPerm")
    public APIResponse<Object> addPerm(@RequestBody PermEntity permEntity) {
        logger.info("enter PermController.addPerm() params = " + permEntity);
        this.permService.addPerm(permEntity);
        return APIResponse.success();
    }

    @PostMapping("/deletePerm")
    public APIResponse<Object> deletePerm(@RequestBody PermEntity permEntity) {
        logger.info("enter PermController.deletePerm() params = " + permEntity);
        this.permService.deletePerm(permEntity);
        return APIResponse.success();
    }

    @PostMapping("/updatePerm")
    public APIResponse<Object> updatePerm(@RequestBody PermEntity permEntity) {
        logger.info("enter PermController.updatePerm() params = " + permEntity);
        this.permService.updatePerm(permEntity);
        return APIResponse.success();
    }

    @PostMapping("/getPermTree")
    public APIResponse<List<TreeEntity>> getPermTree(@RequestBody PermEntity permEntity) {
        logger.info("enter PermController.getPermTree() params = " + permEntity);
        return APIResponse.success(this.permService.getPermTree(permEntity));
    }

    @PostMapping("/getChildrenList")
    public APIResponse<List<TreeEntity>> getChildrenList(@RequestBody PermEntity permEntity) {
        logger.info("enter PermController.getChildrenList() params = " + permEntity);
        return APIResponse.success(this.permService.getChildrenList(permEntity));
    }

}
