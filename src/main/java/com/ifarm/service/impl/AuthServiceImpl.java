package com.ifarm.service.impl;

import com.ifarm.common.exception.BusinessException;
import com.ifarm.common.result.ResultCode;
import com.ifarm.common.util.BeanUtils;
import com.ifarm.common.util.JwtUtil;
import com.ifarm.dto.auth.LoginDTO;
import com.ifarm.dto.auth.PasswordChangeDTO;
import com.ifarm.dto.auth.WechatLoginDTO;
import com.ifarm.dto.wechat.WechatUserInfo;
import com.ifarm.entity.User;
import com.ifarm.service.IAuthService;
import com.ifarm.service.IUserService;
import com.ifarm.service.IWechatService;
import com.ifarm.vo.auth.AuthResponseVO;
import com.ifarm.vo.auth.LoginUserVO;
import com.ifarm.vo.user.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 认证服务实现类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUserService userService;
    private final IWechatService wechatService;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthResponseVO login(LoginDTO loginDTO) {
        // 查找用户
        User user = userService.findByUsernameOrPhone(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.LOGIN_FAILED, "用户名或密码错误");
        }

        // 验证密码
        if (!userService.verifyPassword(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR, "用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(ResultCode.USER_DISABLED, "用户已被禁用");
        }

        // 更新最后登录时间
        userService.updateLastLoginTime(user.getId());

        // 生成token（包含用户类型）
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getUserType());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());

        // 构建响应
        AuthResponseVO response = new AuthResponseVO();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(7200L); // 2小时

        // 设置用户信息
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        response.setUserInfo(convertToUserVO(user));

        // 检查是否需要修改密码（默认密码88888888）
        if (userService.verifyPassword("88888888", user.getPassword())) {
            response.setNeedChangePassword(true);
        }

        log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthResponseVO wechatLogin(WechatLoginDTO wechatLoginDTO) {
        // 通过code获取微信用户信息
        WechatUserInfo wechatUserInfo = wechatService.getWechatUserInfo(wechatLoginDTO.getCode());
        
        // 查找是否已存在该微信用户
        User user = userService.findByOpenid(wechatUserInfo.getOpenid());
        
        boolean isFirstLogin = false;
        if (user == null) {
            // 自动注册新用户
            String nickname = StringUtils.hasText(wechatLoginDTO.getNickname()) ? 
                wechatLoginDTO.getNickname() : wechatUserInfo.getNickname();
            String avatarUrl = StringUtils.hasText(wechatLoginDTO.getAvatarUrl()) ? 
                wechatLoginDTO.getAvatarUrl() : wechatUserInfo.getAvatarUrl();
            Integer gender = wechatLoginDTO.getGender() != null ? 
                wechatLoginDTO.getGender() : wechatUserInfo.getGender();
                
            user = userService.createWechatUser(
                wechatUserInfo.getOpenid(),
                wechatUserInfo.getUnionid(),
                nickname,
                avatarUrl,
                gender
            );
            isFirstLogin = true;
            log.info("微信用户自动注册成功: openid={}, username={}", 
                wechatUserInfo.getOpenid(), user.getUsername());
        } else {
            // 检查用户状态
            if (user.getStatus() == null || user.getStatus() != 1) {
                throw new BusinessException(ResultCode.USER_DISABLED, "用户已被禁用");
            }
            
            // 更新最后登录时间
            userService.updateLastLoginTime(user.getId());
        }

        // 生成token
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());

        // 构建响应
        AuthResponseVO response = new AuthResponseVO();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(7200L); // 2小时
        response.setUserInfo(convertToUserVO(user));
        response.setFirstLogin(isFirstLogin);
        
        // 微信用户默认需要修改密码
        response.setNeedChangePassword(true);

        log.info("微信用户登录成功: userId={}, openid={}", user.getId(), wechatUserInfo.getOpenid());
        return response;
    }

    @Override
    public AuthResponseVO refreshToken(String refreshToken) {
        // 验证刷新token
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID, "刷新Token无效");
        }

        // 从token中获取用户信息
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String username = jwtUtil.getUsernameFromToken(refreshToken);

        if (userId == null || username == null) {
            throw new BusinessException(ResultCode.TOKEN_INVALID, "刷新Token无效");
        }

        // 验证用户是否存在且状态正常
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(ResultCode.USER_DISABLED, "用户已被禁用");
        }

        // 生成新的token（包含用户类型）
        String newAccessToken = jwtUtil.generateAccessToken(userId, username, user.getUserType());
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, username);

        // 构建响应
        AuthResponseVO response = new AuthResponseVO();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setExpiresIn(7200L); // 2小时
        response.setUserInfo(convertToUserVO(user));

        log.info("Token刷新成功: userId={}", userId);
        return response;
    }

    @Override
    public void logout(String token) {
        // TODO: 实现token黑名单机制
        // 这里可以将token加入黑名单，防止被继续使用
        // 由于JWT是无状态的，简单的实现可以使用Redis存储黑名单
        log.info("用户退出登录");
    }

    @Override
    public UserVO getCurrentUserProfile(Long userId) {
        return userService.getUserProfile(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, PasswordChangeDTO passwordChangeDTO) {
        userService.changePassword(userId, passwordChangeDTO);
    }

    /**
     * 转换User实体为UserVO
     * 
     * @param user 用户实体
     * @return 用户视图对象
     */
    private UserVO convertToUserVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}
