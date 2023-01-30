package org.xgllhz.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xgllhz.security.config.YmlConfig;
import org.xgllhz.security.util.APIResponse;
import org.xgllhz.security.util.RedisUtils;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: XGLLHZ
 * @date: 2022/7/10 12:20
 * @description: security controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class SecurityController {

    private final String prefix = "security:account:sessions:";

    private final RedisTemplate<String, Object> redisTemplate;

    @RequestMapping("/set")
    public APIResponse<Boolean> test(@RequestParam("value") String value) {
        Boolean result = RedisUtils.addSetValue(redisTemplate, prefix + "xgllhz", value,
                YmlConfig.getRedisExpirationTimeout(), TimeUnit.SECONDS);
        RedisUtils.addSetValue(redisTemplate, prefix + "momo", value,
                YmlConfig.getRedisExpirationTimeout(), TimeUnit.SECONDS);
        return APIResponse.success(result);
    }

    @RequestMapping("/get")
    public APIResponse<Set<Object>> get() {
        Set<Object> result = RedisUtils.getSetValue(redisTemplate, prefix);
        return APIResponse.success(result);
    }

    @RequestMapping("contain")
    public APIResponse<Boolean> contain(@RequestParam("value") String value) {
        Boolean result = RedisUtils.containSetValue(redisTemplate, prefix + "xgllhz", value);
        return APIResponse.success(result);
    }

    @RequestMapping("/log/test")
    public APIResponse<String> log() {
        return APIResponse.success("红衣女妖仙");
    }

    @RequestMapping("/perm/list")
    public APIResponse<String> perm() {
        return APIResponse.success("红衣女妖仙");
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("xgllhzyou"));
    }

}
