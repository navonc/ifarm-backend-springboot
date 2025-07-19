package com.ifarm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifarm.dto.auth.PasswordChangeDTO;
import com.ifarm.dto.user.ProfileUpdateDTO;
import com.ifarm.dto.user.UserCreateDTO;
import com.ifarm.entity.User;
import com.ifarm.vo.user.UserVO;

/**
 * 用户服务接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public interface IUserService extends IService<User> {

    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);

    /**
     * 根据手机号查找用户
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    User findByPhone(String phone);

    /**
     * 根据openid查找用户
     * 
     * @param openid 微信openid
     * @return 用户信息
     */
    User findByOpenid(String openid);

    /**
     * 根据用户名或手机号查找用户
     * 
     * @param usernameOrPhone 用户名或手机号
     * @return 用户信息
     */
    User findByUsernameOrPhone(String usernameOrPhone);

    /**
     * 创建用户
     * 
     * @param userCreateDTO 用户创建信息
     * @return 创建的用户
     */
    User createUser(UserCreateDTO userCreateDTO);

    /**
     * 创建微信用户
     * 
     * @param openid 微信openid
     * @param unionid 微信unionid
     * @param nickname 昵称
     * @param avatarUrl 头像
     * @param gender 性别
     * @return 创建的用户
     */
    User createWechatUser(String openid, String unionid, String nickname, String avatarUrl, Integer gender);

    /**
     * 更新用户信息
     * 
     * @param userId 用户ID
     * @param profileUpdateDTO 更新信息
     * @return 更新后的用户
     */
    User updateUserProfile(Long userId, ProfileUpdateDTO profileUpdateDTO);

    /**
     * 获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户视图对象
     */
    UserVO getUserProfile(Long userId);

    /**
     * 修改密码
     * 
     * @param userId 用户ID
     * @param passwordChangeDTO 密码修改信息
     */
    void changePassword(Long userId, PasswordChangeDTO passwordChangeDTO);

    /**
     * 验证密码
     * 
     * @param rawPassword 原始密码
     * @param encodedPassword 加密密码
     * @return 是否匹配
     */
    boolean verifyPassword(String rawPassword, String encodedPassword);

    /**
     * 加密密码
     * 
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    String encodePassword(String rawPassword);

    /**
     * 更新最后登录时间
     * 
     * @param userId 用户ID
     */
    void updateLastLoginTime(Long userId);

    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查手机号是否存在
     * 
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 检查openid是否存在
     * 
     * @param openid 微信openid
     * @return 是否存在
     */
    boolean existsByOpenid(String openid);
}
