package com.ifarm.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 * 
 * @author ifarm
 * @since 2024-03-15
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    
    // 通用响应码
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    PARAMETER_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    SYSTEM_ERROR(500, "系统错误"),

    // 认证相关响应码
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    TOKEN_INVALID(401, "Token无效"),
    TOKEN_EXPIRED(401, "Token已过期"),
    LOGIN_FAILED(401, "登录失败"),
    PASSWORD_ERROR(401, "密码错误"),

    // 用户相关响应码
    USER_NOT_FOUND(404, "用户不存在"),
    USER_ALREADY_EXISTS(409, "用户已存在"),
    USER_DISABLED(403, "用户已被禁用"),

    // 微信相关响应码
    WECHAT_API_ERROR(500, "微信接口调用失败"),
    WECHAT_CODE_INVALID(400, "微信登录凭证无效"),
    WECHAT_USER_INFO_ERROR(500, "获取微信用户信息失败");
    
    
    /**
     * 响应码
     */
    private final Integer code;
    
    /**
     * 响应消息
     */
    private final String message;
}
