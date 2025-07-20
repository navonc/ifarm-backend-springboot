package com.ifarm.controller;

import com.ifarm.common.result.Result;
import com.ifarm.common.util.JwtUtil;
import com.ifarm.dto.auth.LoginDTO;
import com.ifarm.dto.auth.PasswordChangeDTO;
import com.ifarm.dto.auth.RefreshTokenDTO;
import com.ifarm.dto.auth.WechatLoginDTO;
import com.ifarm.dto.user.ProfileUpdateDTO;
import com.ifarm.service.IAuthService;
import com.ifarm.service.IUserService;
import com.ifarm.vo.auth.AuthResponseVO;
import com.ifarm.vo.user.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {

    private final IAuthService authService;
    private final IUserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 用户密码登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户密码登录", description = "使用用户名/手机号和密码进行登录")
    public Result<AuthResponseVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("用户登录请求: username={}", loginDTO.getUsername());
        AuthResponseVO response = authService.login(loginDTO);
        return Result.success(response);
    }

    /**
     * 微信小程序登录
     */
    @PostMapping("/wechat-login")
    @Operation(summary = "微信小程序登录", description = "使用微信授权码进行登录，如果用户不存在则自动注册")
    public Result<AuthResponseVO> wechatLogin(@Valid @RequestBody WechatLoginDTO wechatLoginDTO) {
        log.info("微信登录请求: code={}", wechatLoginDTO.getCode());
        AuthResponseVO response = authService.wechatLogin(wechatLoginDTO);
        return Result.success(response);
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新Token", description = "使用刷新Token获取新的访问Token")
    public Result<AuthResponseVO> refreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        log.info("Token刷新请求");
        AuthResponseVO response = authService.refreshToken(refreshTokenDTO.getRefreshToken());
        return Result.success(response);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "用户退出登录")
    public Result<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token != null) {
            authService.logout(token);
        }
        log.info("用户退出登录");
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @SecurityRequirement(name = "Authorization")
    public Result<UserVO> getCurrentUserProfile(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("获取用户信息请求: userId={}", userId);
        UserVO userProfile = authService.getCurrentUserProfile(userId);
        return Result.success(userProfile);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的基本信息")
    @SecurityRequirement(name = "Authorization")
    public Result<UserVO> updateUserProfile(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateDTO profileUpdateDTO) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("更新用户信息请求: userId={}", userId);
        userService.updateUserProfile(userId, profileUpdateDTO);
        UserVO userProfile = authService.getCurrentUserProfile(userId);
        return Result.success(userProfile);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    @SecurityRequirement(name = "Authorization")
    public Result<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody PasswordChangeDTO passwordChangeDTO) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("修改密码请求: userId={}", userId);
        authService.changePassword(userId, passwordChangeDTO);
        return Result.success();
    }

    /**
     * 验证Token有效性
     */
    @GetMapping("/validate")
    @Operation(summary = "验证Token", description = "验证当前Token是否有效")
    @SecurityRequirement(name = "Authorization")
    public Result<Boolean> validateToken(
            @Parameter(description = "用户ID", hidden = true) Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.debug("Token验证请求: userId={}", userId);
        return Result.success(true);
    }
}
