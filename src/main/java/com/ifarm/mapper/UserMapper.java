package com.ifarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifarm.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND deleted = 0")
    User findByUsername(@Param("username") String username);

    /**
     * 根据手机号查找用户
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE phone = #{phone} AND deleted = 0")
    User findByPhone(@Param("phone") String phone);

    /**
     * 根据openid查找用户
     * 
     * @param openid 微信openid
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE openid = #{openid} AND deleted = 0")
    User findByOpenid(@Param("openid") String openid);

    /**
     * 根据用户名或手机号查找用户
     * 
     * @param usernameOrPhone 用户名或手机号
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE (username = #{usernameOrPhone} OR phone = #{usernameOrPhone}) AND deleted = 0")
    User findByUsernameOrPhone(@Param("usernameOrPhone") String usernameOrPhone);

    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 存在数量
     */
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username} AND deleted = 0")
    int countByUsername(@Param("username") String username);

    /**
     * 检查手机号是否存在
     * 
     * @param phone 手机号
     * @return 存在数量
     */
    @Select("SELECT COUNT(*) FROM users WHERE phone = #{phone} AND deleted = 0")
    int countByPhone(@Param("phone") String phone);

    /**
     * 检查openid是否存在
     * 
     * @param openid 微信openid
     * @return 存在数量
     */
    @Select("SELECT COUNT(*) FROM users WHERE openid = #{openid} AND deleted = 0")
    int countByOpenid(@Param("openid") String openid);
}
