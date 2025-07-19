package com.ifarm.service;

import com.ifarm.dto.auth.LoginDTO;
import com.ifarm.dto.auth.PasswordChangeDTO;
import com.ifarm.dto.auth.WechatLoginDTO;
import com.ifarm.vo.auth.AuthResponseVO;
import com.ifarm.vo.user.UserVO;

/**
 * 认证服务接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public interface IAuthService {

    /**
     * 用户密码登录
     * 
     * @param loginDTO 登录信息
     * @return 认证响应
     */
    AuthResponseVO login(LoginDTO loginDTO);

    /**
     * 微信小程序登录
     * 
     * @param wechatLoginDTO 微信登录信息
     * @return 认证响应
     */
    AuthResponseVO wechatLogin(WechatLoginDTO wechatLoginDTO);

    /**
     * 刷新Token
     * 
     * @param refreshToken 刷新Token
     * @return 认证响应
     */
    AuthResponseVO refreshToken(String refreshToken);

    /**
     * 退出登录
     * 
     * @param token 访问Token
     */
    void logout(String token);

    /**
     * 获取当前用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getCurrentUserProfile(Long userId);

    /**
     * 修改密码
     * 
     * @param userId 用户ID
     * @param passwordChangeDTO 密码修改信息
     */
    void changePassword(Long userId, PasswordChangeDTO passwordChangeDTO);
}
