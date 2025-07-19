package com.ifarm.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 刷新Token数据传输对象
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@Schema(name = "RefreshTokenDTO", description = "刷新Token请求")
public class RefreshTokenDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 刷新Token
     */
    @NotBlank(message = "刷新Token不能为空")
    @Schema(description = "刷新Token", example = "eyJhbGciOiJIUzUxMiJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;
}
