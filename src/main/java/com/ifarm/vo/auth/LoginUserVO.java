package com.ifarm.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录用户信息视图对象
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@Schema(name = "LoginUserVO", description = "登录用户信息")
public class LoginUserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "ifarm_user")
    private String username;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称", example = "农场小主")
    private String nickname;

    /**
     * 头像地址
     */
    @Schema(description = "头像地址", example = "https://example.com/avatar.jpg")
    private String avatar;

    /**
     * 手机号
     */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 用户类型：1-普通用户，2-农场主，3-管理员
     */
    @Schema(description = "用户类型", example = "1", allowableValues = {"1", "2", "3"})
    private Integer userType;

    /**
     * 用户类型描述
     */
    @Schema(description = "用户类型描述", example = "普通用户")
    private String userTypeDesc;

    /**
     * 性别：0-未知，1-男，2-女
     */
    @Schema(description = "性别", example = "1", allowableValues = {"0", "1", "2"})
    private Integer gender;

    /**
     * 性别描述
     */
    @Schema(description = "性别描述", example = "男")
    private String genderDesc;

    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间", example = "2024-01-01 12:00:00")
    private LocalDateTime lastLoginTime;

    /**
     * 设置用户类型描述
     */
    public void setUserType(Integer userType) {
        this.userType = userType;
        if (userType != null) {
            switch (userType) {
                case 1 -> this.userTypeDesc = "普通用户";
                case 2 -> this.userTypeDesc = "农场主";
                case 3 -> this.userTypeDesc = "管理员";
                default -> this.userTypeDesc = "未知";
            }
        } else {
            this.userTypeDesc = "未知";
        }
    }

    /**
     * 设置性别描述
     */
    public void setGender(Integer gender) {
        this.gender = gender;
        if (gender != null) {
            switch (gender) {
                case 0 -> this.genderDesc = "未知";
                case 1 -> this.genderDesc = "男";
                case 2 -> this.genderDesc = "女";
                default -> this.genderDesc = "未知";
            }
        } else {
            this.genderDesc = "未知";
        }
    }
}
