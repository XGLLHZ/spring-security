package org.xgllhz.security.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xgllhz.security.entity.UserEntity;
import org.xgllhz.security.exception.GlobalException;
import org.xgllhz.security.service.IUserService;
import org.xgllhz.security.util.APIResponse;

/**
 * @author: XGLLHZ
 * @date: 2022/8/26 14:39
 * @description: user controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/user")
public class UserController {

    private static final Log logger = LogFactory.getLog(UserController.class);

    private final IUserService userService;

    @PostMapping("/listUser")
    public APIResponse<IPage<UserEntity>> listUser(@RequestBody UserEntity userEntity) {
        logger.info("enter UserController.listUser() params = " +  userEntity);
        return APIResponse.success(this.userService.listUser(userEntity));
    }

    @PostMapping("/getUser")
    public APIResponse<UserEntity> getUser(@RequestBody UserEntity userEntity) {
        logger.info("enter UserController.getUser() params = " + userEntity);
        return APIResponse.success(this.userService.getUser(userEntity));
    }

    @PostMapping("/addUser")
    public APIResponse<Object> addUser(@RequestBody UserEntity userEntity) {
        logger.info("enter UserController.addUser() params = " + userEntity);

        userEntity.setPassword("123456");

        this.userService.addUser(userEntity);
        return APIResponse.success();
    }

    @PostMapping("/deleteUser")
    public APIResponse<Object> deleteUser(@RequestBody UserEntity userEntity) {
        logger.info("enter UserController.deleteUser() params = " + userEntity);
        this.userService.deleteUser(userEntity);
        return APIResponse.success();
    }

    @PostMapping("/updateUser")
    public APIResponse<Object> updateUser(@RequestBody UserEntity userEntity) {
        logger.info("enter UserController.updateUser() params = " + userEntity);

        if (!ObjectUtils.isEmpty(userEntity.getPassword())) {
            throw new GlobalException("The param error!");
        }

        this.userService.updateUser(userEntity);
        return APIResponse.success();
    }

    @PostMapping("/updatePassword")
    public APIResponse<Object> updatePassword(@RequestBody UserEntity userEntity) {
        logger.info("enter UserController.updatePassword() params = " + userEntity);
        this.userService.updatePassword(userEntity);
        return APIResponse.success();
    }

}
