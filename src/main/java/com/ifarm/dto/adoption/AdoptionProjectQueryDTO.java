package com.ifarm.dto.adoption;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 认养项目查询DTO
 * 
 * @author ifarm
 * @since 2025-01-20
 */
@Data
@Schema(name = "AdoptionProjectQueryDTO", description = "认养项目查询")
public class AdoptionProjectQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目名称（模糊查询）
     */
    @Schema(description = "项目名称（模糊查询）", example = "蔬菜")
    private String name;

    /**
     * 农场ID
     */
    @Schema(description = "农场ID", example = "1")
    private Long farmId;

    /**
     * 地块ID
     */
    @Schema(description = "地块ID", example = "1")
    private Long plotId;

    /**
     * 作物ID
     */
    @Schema(description = "作物ID", example = "1")
    private Long cropId;

    /**
     * 项目状态
     * 1-草稿 2-认养中 3-已满员 4-进行中 5-已完成 6-已取消
     */
    @Min(value = 1, message = "项目状态值无效")
    @Max(value = 6, message = "项目状态值无效")
    @Schema(description = "项目状态", example = "2", 
            allowableValues = {"1", "2", "3", "4", "5", "6"})
    private Integer projectStatus;

    /**
     * 最小价格
     */
    @Schema(description = "最小价格", example = "100.00")
    private BigDecimal minPrice;

    /**
     * 最大价格
     */
    @Schema(description = "最大价格", example = "500.00")
    private BigDecimal maxPrice;

    /**
     * 开始时间范围-开始
     */
    @Schema(description = "开始时间范围-开始", example = "2025-01-01T00:00:00")
    private LocalDateTime startTimeBegin;

    /**
     * 开始时间范围-结束
     */
    @Schema(description = "开始时间范围-结束", example = "2025-12-31T23:59:59")
    private LocalDateTime startTimeEnd;

    /**
     * 是否有可用单元
     */
    @Schema(description = "是否有可用单元", example = "true")
    private Boolean hasAvailableUnits;

    /**
     * 排序字段
     * createTime-创建时间, adoptionPrice-价格, totalUnits-单元数, startTime-开始时间
     */
    @Schema(description = "排序字段", example = "createTime", 
            allowableValues = {"createTime", "adoptionPrice", "totalUnits", "startTime"})
    private String sortField;

    /**
     * 排序方向
     * asc-升序, desc-降序
     */
    @Schema(description = "排序方向", example = "desc", 
            allowableValues = {"asc", "desc"})
    private String sortOrder;

    /**
     * 关键词搜索（项目名称、描述、标签）
     */
    @Schema(description = "关键词搜索", example = "有机")
    private String keyword;
}
