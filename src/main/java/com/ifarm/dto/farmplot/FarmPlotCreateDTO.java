package com.ifarm.dto.farmplot;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 地块创建DTO
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@Schema(description = "地块创建请求")
public class FarmPlotCreateDTO {

    @NotBlank(message = "地块名称不能为空")
    @Size(max = 100, message = "地块名称长度不能超过100个字符")
    @Schema(description = "地块名称", example = "A区1号地块")
    private String plotName;

    @NotNull(message = "农场ID不能为空")
    @Schema(description = "农场ID", example = "1")
    private Long farmId;

    @Positive(message = "地块面积必须为正数")
    @Schema(description = "地块面积（亩）", example = "5.2")
    private BigDecimal area;

    @Size(max = 200, message = "地块位置长度不能超过200个字符")
    @Schema(description = "地块位置", example = "农场东北角")
    private String location;

    @Size(max = 50, message = "土壤类型长度不能超过50个字符")
    @Schema(description = "土壤类型", example = "黑土")
    private String soilType;

    @DecimalMin(value = "0.0", message = "土壤pH值不能为负数")
    @DecimalMax(value = "14.0", message = "土壤pH值不能超过14")
    @Schema(description = "土壤pH值", example = "6.5")
    private BigDecimal soilPh;

    @Size(max = 50, message = "灌溉方式长度不能超过50个字符")
    @Schema(description = "灌溉方式", example = "滴灌")
    private String irrigationType;

    @Size(max = 500, message = "地块描述长度不能超过500个字符")
    @Schema(description = "地块描述", example = "适合种植叶菜类作物")
    private String description;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled = true;

    @Min(value = 1, message = "地块状态值无效")
    @Max(value = 4, message = "地块状态值无效")
    @Schema(description = "地块状态（1-空闲 2-种植中 3-收获中 4-休耕）", example = "1")
    private Integer plotStatus = 1;

    @Size(max = 200, message = "种植历史长度不能超过200个字符")
    @Schema(description = "种植历史", example = "去年种植过西红柿")
    private String plantingHistory;

    @Size(max = 100, message = "环境条件长度不能超过100个字符")
    @Schema(description = "环境条件", example = "光照充足，通风良好")
    private String environmentalConditions;
}
