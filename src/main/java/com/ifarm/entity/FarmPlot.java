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
 * 农场地块实体类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("farm_plots")
@Schema(name = "FarmPlot", description = "农场地块信息")
public class FarmPlot implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 地块ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "地块ID", example = "1")
    private Long id;

    /**
     * 农场ID
     */
    @TableField("farm_id")
    @Schema(description = "农场ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long farmId;

    /**
     * 地块名称
     */
    @TableField("name")
    @Schema(description = "地块名称", example = "A区1号地块", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 地块描述
     */
    @TableField("description")
    @Schema(description = "地块描述", example = "向阳地块，土壤肥沃，适合种植蔬菜")
    private String description;

    /**
     * 地块面积（平方米）
     */
    @TableField("area")
    @Schema(description = "地块面积", example = "1000.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal area;

    /**
     * 土壤类型
     */
    @TableField("soil_type")
    @Schema(description = "土壤类型", example = "黑土")
    private String soilType;

    /**
     * 灌溉方式
     */
    @TableField("irrigation_type")
    @Schema(description = "灌溉方式", example = "滴灌")
    private String irrigationType;

    /**
     * 位置信息（JSON格式）
     */
    @TableField("location_info")
    @Schema(description = "位置信息", example = "{\"latitude\": 40.123456, \"longitude\": 116.123456}")
    private String locationInfo;

    /**
     * 地块图片（JSON数组）
     */
    @TableField("images")
    @Schema(description = "地块图片", example = "[\"/images/plots/plot1.jpg\", \"/images/plots/plot2.jpg\"]")
    private String images;

    /**
     * 状态：0-禁用，1-可用，2-使用中
     */
    @TableField("status")
    @Schema(description = "状态", example = "1", allowableValues = {"0", "1", "2"})
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
