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
 * 认养项目实体类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("adoption_projects")
@Schema(name = "AdoptionProject", description = "认养项目信息")
public class AdoptionProject implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "项目ID", example = "1")
    private Long id;

    /**
     * 地块ID
     */
    @TableField("plot_id")
    @Schema(description = "地块ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long plotId;

    /**
     * 作物ID
     */
    @TableField("crop_id")
    @Schema(description = "作物ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long cropId;

    /**
     * 项目名称
     */
    @TableField("name")
    @Schema(description = "项目名称", example = "有机番茄认养项目", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 项目描述
     */
    @TableField("description")
    @Schema(description = "项目描述", example = "精选优质番茄品种，全程有机种植，预计产量5kg/单元")
    private String description;

    /**
     * 总单元数
     */
    @TableField("total_units")
    @Schema(description = "总单元数", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalUnits;

    /**
     * 可认养单元数
     */
    @TableField("available_units")
    @Schema(description = "可认养单元数", example = "80", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer availableUnits;

    /**
     * 单元面积（平方米）
     */
    @TableField("unit_area")
    @Schema(description = "单元面积", example = "10.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal unitArea;

    /**
     * 单元认养价格
     */
    @TableField("unit_price")
    @Schema(description = "单元认养价格", example = "299.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal unitPrice;

    /**
     * 预期单元产量（kg）
     */
    @TableField("expected_yield")
    @Schema(description = "预期单元产量", example = "5.00")
    private BigDecimal expectedYield;

    /**
     * 种植日期
     */
    @TableField("planting_date")
    @Schema(description = "种植日期", example = "2024-03-01")
    private LocalDate plantingDate;

    /**
     * 预期收获日期
     */
    @TableField("expected_harvest_date")
    @Schema(description = "预期收获日期", example = "2024-06-01")
    private LocalDate expectedHarvestDate;

    /**
     * 项目状态：1-筹备中，2-认养中，3-种植中，4-收获中，5-已完成，6-已取消
     */
    @TableField("project_status")
    @Schema(description = "项目状态", example = "2", allowableValues = {"1", "2", "3", "4", "5", "6"})
    private Integer projectStatus;

    /**
     * 项目封面图
     */
    @TableField("cover_image")
    @Schema(description = "项目封面图", example = "/images/projects/project_cover.jpg")
    private String coverImage;

    /**
     * 项目图片（JSON数组）
     */
    @TableField("images")
    @Schema(description = "项目图片", example = "[\"/images/projects/project1.jpg\", \"/images/projects/project2.jpg\"]")
    private String images;

    /**
     * 种植计划
     */
    @TableField("planting_plan")
    @Schema(description = "种植计划", example = "3月播种，4月移栽，6月收获")
    private String plantingPlan;

    /**
     * 养护说明
     */
    @TableField("care_instructions")
    @Schema(description = "养护说明", example = "定期浇水施肥，注意病虫害防治")
    private String careInstructions;

    /**
     * 收获说明
     */
    @TableField("harvest_instructions")
    @Schema(description = "收获说明", example = "果实成熟后及时采摘，保证新鲜度")
    private String harvestInstructions;

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
