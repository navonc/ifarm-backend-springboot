package com.ifarm.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 收获记录实体类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("harvest_records")
@Schema(name = "HarvestRecord", description = "收获记录信息")
public class HarvestRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 收获记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "收获记录ID", example = "1")
    private Long id;

    /**
     * 项目ID
     */
    @TableField("project_id")
    @Schema(description = "项目ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long projectId;

    /**
     * 单元ID（可选，整体收获时为空）
     */
    @TableField("unit_id")
    @Schema(description = "单元ID", example = "1")
    private Long unitId;

    /**
     * 收获日期
     */
    @TableField("harvest_date")
    @Schema(description = "收获日期", example = "2024-06-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate harvestDate;

    /**
     * 收获数量（kg）
     */
    @TableField("harvest_quantity")
    @Schema(description = "收获数量", example = "5.20", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal harvestQuantity;

    /**
     * 品质等级
     */
    @TableField("quality_grade")
    @Schema(description = "品质等级", example = "A级", allowableValues = {"A级", "B级", "C级"})
    private String qualityGrade;

    /**
     * 品质评分
     */
    @TableField("quality_score")
    @Schema(description = "品质评分", example = "9.5")
    private BigDecimal qualityScore;

    /**
     * 收获方式
     */
    @TableField("harvest_method")
    @Schema(description = "收获方式", example = "人工采摘")
    private String harvestMethod;

    /**
     * 储存方式
     */
    @TableField("storage_method")
    @Schema(description = "储存方式", example = "冷藏保鲜")
    private String storageMethod;

    /**
     * 包装类型
     */
    @TableField("packaging_type")
    @Schema(description = "包装类型", example = "环保包装盒")
    private String packagingType;

    /**
     * 收获备注
     */
    @TableField("harvest_notes")
    @Schema(description = "收获备注", example = "果实饱满，色泽鲜艳，品质优良")
    private String harvestNotes;

    /**
     * 收获图片（JSON数组）
     */
    @TableField("images")
    @Schema(description = "收获图片", example = "[\"/images/harvest/20240601_1.jpg\", \"/images/harvest/20240601_2.jpg\"]")
    private String images;

    /**
     * 收获视频（JSON数组）
     */
    @TableField("videos")
    @Schema(description = "收获视频", example = "[\"/videos/harvest/20240601_harvest.mp4\"]")
    private String videos;

    /**
     * 收获人ID
     */
    @TableField("harvester_id")
    @Schema(description = "收获人ID", example = "1")
    private Long harvesterId;

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
