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
 * 用户认养记录实体类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("adoption_records")
@Schema(name = "AdoptionRecord", description = "用户认养记录信息")
public class AdoptionRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 认养记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "认养记录ID", example = "1")
    private Long id;

    /**
     * 订单ID
     */
    @TableField("order_id")
    @Schema(description = "订单ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    /**
     * 项目ID
     */
    @TableField("project_id")
    @Schema(description = "项目ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long projectId;

    /**
     * 单元ID
     */
    @TableField("unit_id")
    @Schema(description = "单元ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long unitId;

    /**
     * 认养状态：1-已认养，2-种植中，3-待收获，4-已收获，5-已完成
     */
    @TableField("adoption_status")
    @Schema(description = "认养状态", example = "1", allowableValues = {"1", "2", "3", "4", "5"})
    private Integer adoptionStatus;

    /**
     * 认养日期
     */
    @TableField("adoption_date")
    @Schema(description = "认养日期", example = "2024-01-01 12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime adoptionDate;

    /**
     * 种植日期
     */
    @TableField("planting_date")
    @Schema(description = "种植日期", example = "2024-03-01 08:00:00")
    private LocalDateTime plantingDate;

    /**
     * 收获日期
     */
    @TableField("harvest_date")
    @Schema(description = "收获日期", example = "2024-06-01 10:00:00")
    private LocalDateTime harvestDate;

    /**
     * 实际产量（kg）
     */
    @TableField("actual_yield")
    @Schema(description = "实际产量", example = "5.20")
    private BigDecimal actualYield;

    /**
     * 品质等级
     */
    @TableField("quality_grade")
    @Schema(description = "品质等级", example = "A级", allowableValues = {"A级", "B级", "C级"})
    private String qualityGrade;

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
