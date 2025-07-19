package com.ifarm.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户更新数据传输对象
 * 用于接收前端传递的更新用户数据
 * 
 * @author ifarm
 * @since 2025-7-18
 */
@Data
@Schema(name = "UserUpdateDTO", description = "用户更新数据传输对象")
public class UserUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（更新时必填）
     */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 用户名
     */
    @Size(max = 100, message = "用户名长度不能超过100个字符")
    @Schema(description = "用户名", example = "ifarm_user")
    private String username;

    /**
     * 密码（如果不修改密码，可以不传）
     */
    @Size(max = 255, message = "密码长度不能超过255个字符")
    @Schema(description = "密码", example = "123456")
    private String password;

    /**
     * 用户昵称
     */
    @Size(max = 50, message = "用户昵称长度不能超过50个字符")
    @Schema(description = "用户昵称", example = "农场小主")
    private String nickname;

    /**
     * 头像地址
     */
    @Size(max = 500, message = "头像地址长度不能超过500个字符")
    @Schema(description = "头像地址", example = "https://example.com/avatar.jpg")
    private String avatar;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 用户类型：1-普通用户，2-农场主，3-管理员
     */
    @Min(value = 1, message = "用户类型值必须在1-3之间")
    @Max(value = 3, message = "用户类型值必须在1-3之间")
    @Schema(description = "用户类型", example = "1", allowableValues = {"1", "2", "3"})
    private Integer userType;

    /**
     * 性别：0-未知，1-男，2-女
     */
    @Min(value = 0, message = "性别值必须在0-2之间")
    @Max(value = 2, message = "性别值必须在0-2之间")
    @Schema(description = "性别", example = "1", allowableValues = {"0", "1", "2"})
    private Integer gender;
    

    /**
     * 状态：0-禁用，1-正常
     */
    @Min(value = 0, message = "状态值必须在0-1之间")
    @Max(value = 1, message = "状态值必须在0-1之间")
    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;
}
