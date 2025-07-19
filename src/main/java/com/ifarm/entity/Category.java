package com.ifarm.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分类实体类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("categories")
@Schema(name = "Category", description = "分类信息")
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "分类ID", example = "1")
    private Long id;

    /**
     * 父分类ID，0表示顶级分类
     */
    @TableField("parent_id")
    @Schema(description = "父分类ID", example = "0")
    private Long parentId;

    /**
     * 分类名称
     */
    @TableField("name")
    @Schema(description = "分类名称", example = "蔬菜类", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 分类编码
     */
    @TableField("code")
    @Schema(description = "分类编码", example = "vegetables")
    private String code;

    /**
     * 分类图标
     */
    @TableField("icon")
    @Schema(description = "分类图标", example = "/icons/vegetables.png")
    private String icon;

    /**
     * 排序
     */
    @TableField("sort_order")
    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    /**
     * 状态：0-禁用，1-启用
     */
    @TableField("status")
    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

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
