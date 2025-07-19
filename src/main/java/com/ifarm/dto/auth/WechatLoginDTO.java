package com.ifarm.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信登录数据传输对象
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@Schema(name = "WechatLoginDTO", description = "微信登录请求")
public class WechatLoginDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 微信登录凭证code
     */
    @NotBlank(message = "微信登录凭证不能为空")
    @Schema(description = "微信登录凭证code", example = "0123456789abcdef", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    /**
     * 用户昵称（可选，用于自动注册）
     */
    @Schema(description = "用户昵称", example = "农场小主")
    private String nickname;

    /**
     * 用户头像（可选，用于自动注册）
     */
    @Schema(description = "用户头像", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    /**
     * 用户性别（可选，用于自动注册）
     */
    @Schema(description = "用户性别", example = "1", allowableValues = {"0", "1", "2"})
    private Integer gender;
}
