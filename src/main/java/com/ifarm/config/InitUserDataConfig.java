package com.ifarm.config;

import com.ifarm.entity.User;
import com.ifarm.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 初始化用户数据配置
 * 在应用启动时自动创建默认用户账号
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InitUserDataConfig implements CommandLineRunner {

    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化用户数据...");
        
        // 检查是否已经初始化过
        User existingAdmin = userService.findByUsername("admin");
        if (existingAdmin != null) {
            log.info("用户数据已存在，跳过初始化");
            return;
        }

        // 创建初始化用户列表
        List<User> initUsers = createInitUsers();
        
        // 批量保存用户
        for (User user : initUsers) {
            try {
                userService.save(user);
                log.info("创建用户成功: {} - {}", user.getUsername(), user.getNickname());
            } catch (Exception e) {
                log.error("创建用户失败: {} - {}", user.getUsername(), e.getMessage());
            }
        }
        
        log.info("用户数据初始化完成，共创建 {} 个用户", initUsers.size());
    }

    /**
     * 创建初始化用户列表
     */
    private List<User> createInitUsers() {
        String defaultPassword = passwordEncoder.encode("123456");
        LocalDateTime now = LocalDateTime.now();

        return Arrays.asList(
            // 管理员账号
            createUser("admin", defaultPassword, "系统管理员", 3, 1, "13800000001", now),
            
            // 农场负责人账号
            createUser("farm_manager", defaultPassword, "农场负责人", 2, 1, "13800000002", now),
            createUser("farm_owner1", defaultPassword, "张三农场主", 2, 1, "13800000003", now),
            createUser("farm_owner2", defaultPassword, "李四农场主", 2, 2, "13800000004", now),
            
            // 普通用户账号
            createUser("user001", defaultPassword, "普通用户001", 1, 1, "13800000101", now),
            createUser("user002", defaultPassword, "普通用户002", 1, 2, "13800000102", now),
            createUser("user003", defaultPassword, "普通用户003", 1, 1, "13800000103", now),
            createUser("test_user", defaultPassword, "测试用户", 1, 0, "13800000999", now)
        );
    }

    /**
     * 创建用户对象
     */
    private User createUser(String username, String password, String nickname, 
                           Integer userType, Integer gender, String phone, LocalDateTime createTime) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setUserType(userType);
        user.setGender(gender);
        user.setPhone(phone);
        user.setStatus(1); // 正常状态
        user.setCreateTime(createTime);
        user.setUpdateTime(createTime);
        user.setDeleted(0); // 未删除
        return user;
    }
}