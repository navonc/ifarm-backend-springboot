package com.ifarm.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改密码数据传输对象
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@Schema(name = "PasswordChangeDTO", description = "修改密码请求")
public class PasswordChangeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 原密码
     */
    @NotBlank(message = "原密码不能为空")
    @Schema(description = "原密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度必须在6-20个字符之间")
    @Schema(description = "新密码", example = "newpassword", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;

    /**
     * 确认新密码
     */
    @NotBlank(message = "确认密码不能为空")
    @Schema(description = "确认新密码", example = "newpassword", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmPassword;
}
