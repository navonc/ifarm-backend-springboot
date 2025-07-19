package com.ifarm.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作物品种实体类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("crops")
@Schema(name = "Crop", description = "作物品种信息")
public class Crop implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 作物ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "作物ID", example = "1")
    private Long id;

    /**
     * 作物分类ID
     */
    @TableField("category_id")
    @Schema(description = "作物分类ID", example = "1")
    private Long categoryId;

    /**
     * 作物名称
     */
    @TableField("name")
    @Schema(description = "作物名称", example = "番茄", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 品种
     */
    @TableField("variety")
    @Schema(description = "品种", example = "大红番茄")
    private String variety;

    /**
     * 作物描述
     */
    @TableField("description")
    @Schema(description = "作物描述", example = "新鲜有机番茄，口感酸甜，营养丰富")
    private String description;

    /**
     * 生长周期（天）
     */
    @TableField("growth_cycle")
    @Schema(description = "生长周期", example = "90")
    private Integer growthCycle;

    /**
     * 种植季节
     */
    @TableField("planting_season")
    @Schema(description = "种植季节", example = "春季")
    private String plantingSeason;

    /**
     * 收获季节
     */
    @TableField("harvest_season")
    @Schema(description = "收获季节", example = "夏季")
    private String harvestSeason;

    /**
     * 单位产量（kg）
     */
    @TableField("yield_per_unit")
    @Schema(description = "单位产量", example = "5.00")
    private BigDecimal yieldPerUnit;

    /**
     * 营养信息（JSON格式）
     */
    @TableField("nutrition_info")
    @Schema(description = "营养信息", example = "{\"vitamin_c\": \"丰富\", \"fiber\": \"高\"}")
    private String nutritionInfo;

    /**
     * 种植指南
     */
    @TableField("planting_guide")
    @Schema(description = "种植指南", example = "适宜温度20-25℃，需充足阳光")
    private String plantingGuide;

    /**
     * 封面图片
     */
    @TableField("cover_image")
    @Schema(description = "封面图片", example = "/images/crops/tomato.jpg")
    private String coverImage;

    /**
     * 作物图片（JSON数组）
     */
    @TableField("images")
    @Schema(description = "作物图片", example = "[\"/images/crops/tomato1.jpg\", \"/images/crops/tomato2.jpg\"]")
    private String images;

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
