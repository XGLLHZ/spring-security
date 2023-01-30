package org.xgllhz.security.util;

import lombok.Data;

/**
 * @author: XGLLHZ
 * @date: 2022/7/13 22:57
 * @description: api response body
 */
@Data
public class APIResponse<T> {

    private String code;

    private String message;

    private T data;

    public APIResponse(String code, String message) {
        this(code, message, null);
    }

    public APIResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> APIResponse<T> success() {
        return success("请求成功！");
    }

    public static <T> APIResponse<T> success(T data) {
        return success("1", "请求成功", data);
    }

    public static <T> APIResponse<T> success(String message) {
        return success("1", message);
    }

    public static <T> APIResponse<T> success(String code, String message) {
        return success(code, message, null);
    }

    public static <T> APIResponse<T> success(String message, T data) {
        return success("1", message, data);
    }

    public static <T> APIResponse<T> success(String code, String message, T data) {
        return new APIResponse<>(code, message, data);
    }

    public static <T> APIResponse<T> failure() {
        return failure("请求失败！");
    }

    public static <T> APIResponse<T> failure(String message) {
        return failure("-1", message);
    }

    public static <T> APIResponse<T> failure(String code, String message) {
        return failure(code, message, null);
    }

    public static <T> APIResponse<T> failure(String code, String message, T data) {
        return new APIResponse<>(code, message, data);
    }

}
