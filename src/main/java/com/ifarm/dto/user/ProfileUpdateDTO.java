package com.ifarm.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户信息更新数据传输对象
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@Schema(name = "ProfileUpdateDTO", description = "用户信息更新请求")
public class ProfileUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @Size(max = 100, message = "用户名长度不能超过100个字符")
    @Schema(description = "用户名", example = "ifarm_user")
    private String username;

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
     * 性别：0-未知，1-男，2-女
     */
    @Min(value = 0, message = "性别值必须在0-2之间")
    @Max(value = 2, message = "性别值必须在0-2之间")
    @Schema(description = "性别", example = "1", allowableValues = {"0", "1", "2"})
    private Integer gender;
}
