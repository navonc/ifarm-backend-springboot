package com.ifarm.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.common.result.ResultCode;
import com.ifarm.common.util.BeanUtils;
import com.ifarm.dto.auth.PasswordChangeDTO;
import com.ifarm.dto.user.ProfileUpdateDTO;
import com.ifarm.dto.user.UserCreateDTO;
import com.ifarm.entity.User;
import com.ifarm.mapper.UserMapper;
import com.ifarm.service.IUserService;
import com.ifarm.vo.user.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        return userMapper.findByUsername(username);
    }

    @Override
    public User findByPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return null;
        }
        return userMapper.findByPhone(phone);
    }

    @Override
    public User findByOpenid(String openid) {
        if (!StringUtils.hasText(openid)) {
            return null;
        }
        return userMapper.findByOpenid(openid);
    }

    @Override
    public User findByUsernameOrPhone(String usernameOrPhone) {
        if (!StringUtils.hasText(usernameOrPhone)) {
            return null;
        }
        return userMapper.findByUsernameOrPhone(usernameOrPhone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User createUser(UserCreateDTO userCreateDTO) {
        // 检查用户名是否已存在
        if (StringUtils.hasText(userCreateDTO.getUsername()) && existsByUsername(userCreateDTO.getUsername())) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "用户名已存在");
        }

        // 检查手机号是否已存在
        if (StringUtils.hasText(userCreateDTO.getPhone()) && existsByPhone(userCreateDTO.getPhone())) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "手机号已存在");
        }

        User user = new User();
        BeanUtils.copyProperties(userCreateDTO, user);
        
        // 加密密码
        if (StringUtils.hasText(userCreateDTO.getPassword())) {
            user.setPassword(encodePassword(userCreateDTO.getPassword()));
        }
        
        // 设置默认值
        user.setStatus(1); // 正常状态
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        if (save(user)) {
            log.info("用户创建成功: {}", user.getUsername());
            return user;
        } else {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "用户创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User createWechatUser(String openid, String unionid, String nickname, String avatarUrl, Integer gender) {
        // 检查openid是否已存在
        if (existsByOpenid(openid)) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "微信用户已存在");
        }

        User user = new User();
        user.setOpenid(openid);
        user.setUnionid(unionid);
        user.setNickname(nickname);
        user.setAvatar(avatarUrl);
        user.setGender(gender);
        
        // 生成用户名：微信昵称 + 随机数
        String username = generateUniqueUsername(nickname);
        user.setUsername(username);
        
        // 设置默认密码：88888888
        user.setPassword(encodePassword("88888888"));
        
        // 设置默认值
        user.setUserType(1); // 普通用户
        user.setStatus(1); // 正常状态
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        if (save(user)) {
            log.info("微信用户创建成功: openid={}, username={}", openid, username);
            return user;
        } else {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "微信用户创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateUserProfile(Long userId, ProfileUpdateDTO profileUpdateDTO) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }

        // 检查用户名是否已被其他用户使用
        if (StringUtils.hasText(profileUpdateDTO.getUsername()) && 
            !profileUpdateDTO.getUsername().equals(user.getUsername()) &&
            existsByUsername(profileUpdateDTO.getUsername())) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "用户名已存在");
        }

        // 检查手机号是否已被其他用户使用
        if (StringUtils.hasText(profileUpdateDTO.getPhone()) && 
            !profileUpdateDTO.getPhone().equals(user.getPhone()) &&
            existsByPhone(profileUpdateDTO.getPhone())) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "手机号已存在");
        }

        BeanUtils.copyProperties(profileUpdateDTO, user);
        user.setUpdateTime(LocalDateTime.now());

        if (updateById(user)) {
            log.info("用户信息更新成功: userId={}", userId);
            return user;
        } else {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "用户信息更新失败");
        }
    }

    @Override
    public UserVO getUserProfile(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, PasswordChangeDTO passwordChangeDTO) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }

        // 验证原密码
        if (!verifyPassword(passwordChangeDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR, "原密码错误");
        }

        // 验证新密码确认
        if (!passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getConfirmPassword())) {
            throw new BusinessException(ResultCode.PARAMETER_ERROR, "新密码与确认密码不一致");
        }

        // 更新密码
        user.setPassword(encodePassword(passwordChangeDTO.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());

        if (updateById(user)) {
            log.info("用户密码修改成功: userId={}", userId);
        } else {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "密码修改失败");
        }
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginTime(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.countByUsername(username) > 0;
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userMapper.countByPhone(phone) > 0;
    }

    @Override
    public boolean existsByOpenid(String openid) {
        return userMapper.countByOpenid(openid) > 0;
    }

    /**
     * 生成唯一用户名
     * 
     * @param nickname 昵称
     * @return 唯一用户名
     */
    private String generateUniqueUsername(String nickname) {
        String baseUsername = StringUtils.hasText(nickname) ? nickname : "用户";
        String username = baseUsername + RandomUtil.randomNumbers(6);
        
        // 确保用户名唯一
        int attempts = 0;
        while (existsByUsername(username) && attempts < 10) {
            username = baseUsername + RandomUtil.randomNumbers(6);
            attempts++;
        }
        
        return username;
    }
}
