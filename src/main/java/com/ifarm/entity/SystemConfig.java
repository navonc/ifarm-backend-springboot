package com.ifarm.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统配置实体类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("system_configs")
@Schema(name = "SystemConfig", description = "系统配置信息")
public class SystemConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "配置ID", example = "1")
    private Long id;

    /**
     * 配置键
     */
    @TableField("config_key")
    @Schema(description = "配置键", example = "site_name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configKey;

    /**
     * 配置值
     */
    @TableField("config_value")
    @Schema(description = "配置值", example = "iFarm智慧农场")
    private String configValue;

    /**
     * 配置描述
     */
    @TableField("config_desc")
    @Schema(description = "配置描述", example = "网站名称")
    private String configDesc;

    /**
     * 配置类型：string,number,boolean,json
     */
    @TableField("config_type")
    @Schema(description = "配置类型", example = "string", allowableValues = {"string", "number", "boolean", "json"})
    private String configType;

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
}
