package com.ifarm.vo.auth;

import com.ifarm.vo.user.UserVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 认证响应视图对象
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@Schema(name = "AuthResponseVO", description = "认证响应")
public class AuthResponseVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 访问Token
     */
    @Schema(description = "访问Token", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String accessToken;

    /**
     * 刷新Token
     */
    @Schema(description = "刷新Token", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String refreshToken;

    /**
     * Token类型
     */
    @Schema(description = "Token类型", example = "Bearer")
    private String tokenType = "Bearer";

    /**
     * 访问Token过期时间（秒）
     */
    @Schema(description = "访问Token过期时间（秒）", example = "7200")
    private Long expiresIn;

    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private UserVO userInfo;

    /**
     * 是否首次登录
     */
    @Schema(description = "是否首次登录", example = "false")
    private Boolean firstLogin = false;

    /**
     * 是否需要修改密码
     */
    @Schema(description = "是否需要修改密码", example = "false")
    private Boolean needChangePassword = false;
}
