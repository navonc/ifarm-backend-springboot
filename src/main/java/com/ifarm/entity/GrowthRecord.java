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
 * 生长记录实体类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("growth_records")
@Schema(name = "GrowthRecord", description = "生长记录信息")
public class GrowthRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "记录ID", example = "1")
    private Long id;

    /**
     * 项目ID
     */
    @TableField("project_id")
    @Schema(description = "项目ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long projectId;

    /**
     * 记录日期
     */
    @TableField("record_date")
    @Schema(description = "记录日期", example = "2024-03-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate recordDate;

    /**
     * 生长阶段
     */
    @TableField("growth_stage")
    @Schema(description = "生长阶段", example = "幼苗期")
    private String growthStage;

    /**
     * 生长状态
     */
    @TableField("growth_status")
    @Schema(description = "生长状态", example = "良好")
    private String growthStatus;

    /**
     * 植株高度（cm）
     */
    @TableField("height")
    @Schema(description = "植株高度", example = "15.50")
    private BigDecimal height;

    /**
     * 天气情况
     */
    @TableField("weather")
    @Schema(description = "天气情况", example = "晴天")
    private String weather;

    /**
     * 最高温度（℃）
     */
    @TableField("temperature_high")
    @Schema(description = "最高温度", example = "25")
    private Integer temperatureHigh;

    /**
     * 最低温度（℃）
     */
    @TableField("temperature_low")
    @Schema(description = "最低温度", example = "18")
    private Integer temperatureLow;

    /**
     * 湿度（%）
     */
    @TableField("humidity")
    @Schema(description = "湿度", example = "65")
    private Integer humidity;

    /**
     * 浇水量（L）
     */
    @TableField("watering_amount")
    @Schema(description = "浇水量", example = "50.00")
    private BigDecimal wateringAmount;

    /**
     * 施肥类型
     */
    @TableField("fertilizer_type")
    @Schema(description = "施肥类型", example = "有机肥")
    private String fertilizerType;

    /**
     * 施肥量（kg）
     */
    @TableField("fertilizer_amount")
    @Schema(description = "施肥量", example = "2.50")
    private BigDecimal fertilizerAmount;

    /**
     * 病虫害防治
     */
    @TableField("pest_control")
    @Schema(description = "病虫害防治", example = "喷洒生物农药，预防蚜虫")
    private String pestControl;

    /**
     * 养护活动
     */
    @TableField("care_activities")
    @Schema(description = "养护活动", example = "松土、除草、整枝")
    private String careActivities;

    /**
     * 备注说明
     */
    @TableField("notes")
    @Schema(description = "备注说明", example = "植株生长良好，叶片翠绿")
    private String notes;

    /**
     * 记录图片（JSON数组）
     */
    @TableField("images")
    @Schema(description = "记录图片", example = "[\"/images/growth/20240315_1.jpg\", \"/images/growth/20240315_2.jpg\"]")
    private String images;

    /**
     * 记录视频（JSON数组）
     */
    @TableField("videos")
    @Schema(description = "记录视频", example = "[\"/videos/growth/20240315_growth.mp4\"]")
    private String videos;

    /**
     * 记录人ID
     */
    @TableField("recorder_id")
    @Schema(description = "记录人ID", example = "1")
    private Long recorderId;

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
