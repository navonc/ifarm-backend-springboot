package com.ifarm.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * @author ifarm
 * @since 2025-7-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("users")
@Schema(name = "User", description = "用户信息")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "用户ID", example = "1")
    private Long id;

    /**
     * 微信openid
     */
    @TableField("openid")
    @Schema(description = "微信openid", example = "o1234567890abcdef")
    private String openid;

    /**
     * 微信unionid
     */
    @TableField("unionid")
    @Schema(description = "微信unionid", example = "u1234567890abcdef")
    private String unionid;

    /**
     * 用户名
     */
    @TableField("username")
    @Schema(description = "用户名", example = "ifarm_user", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    @Schema(description = "密码", example = "encrypted_password", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    /**
     * 用户昵称
     */
    @TableField("nickname")
    @Schema(description = "用户昵称", example = "农场小主")
    private String nickname;

    /**
     * 头像地址
     */
    @TableField("avatar")
    @Schema(description = "头像地址", example = "https://example.com/avatar.jpg")
    private String avatar;

    /**
     * 手机号
     */
    @TableField("phone")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 用户类型：1-普通用户，2-农场主，3-管理员
     */
    @TableField("user_type")
    @Schema(description = "用户类型", example = "1", allowableValues = {"1", "2", "3"})
    private Integer userType;

    /**
     * 性别：0-未知，1-男，2-女
     */
    @TableField("gender")
    @Schema(description = "性别", example = "1", allowableValues = {"0", "1", "2"})
    private Integer gender;
    

    /**
     * 状态：0-禁用，1-正常
     */
    @TableField("status")
    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    @Schema(description = "最后登录时间", example = "2024-01-01 12:00:00")
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", example = "2024-01-01 12:00:00")
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @TableField("deleted")
    @TableLogic
    @Schema(description = "是否删除", example = "0", allowableValues = {"0", "1"})
    private Integer deleted;
}
