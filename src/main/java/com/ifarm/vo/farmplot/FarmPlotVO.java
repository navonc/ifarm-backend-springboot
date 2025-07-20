package com.ifarm.vo.farmplot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 地块VO
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@Schema(description = "地块信息")
public class FarmPlotVO {

    @Schema(description = "地块ID", example = "1")
    private Long id;

    @Schema(description = "地块名称", example = "A区1号地块")
    private String plotName;

    @Schema(description = "农场ID", example = "1")
    private Long farmId;

    @Schema(description = "农场名称", example = "绿野有机农场")
    private String farmName;

    @Schema(description = "地块面积（亩）", example = "5.2")
    private BigDecimal area;

    @Schema(description = "地块位置", example = "农场东北角")
    private String location;

    @Schema(description = "土壤类型", example = "黑土")
    private String soilType;

    @Schema(description = "土壤pH值", example = "6.5")
    private BigDecimal soilPh;

    @Schema(description = "灌溉方式", example = "滴灌")
    private String irrigationType;

    @Schema(description = "地块描述", example = "适合种植叶菜类作物")
    private String description;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

    @Schema(description = "地块状态（1-空闲 2-种植中 3-收获中 4-休耕）", example = "1")
    private Integer plotStatus;

    @Schema(description = "地块状态名称", example = "空闲")
    private String plotStatusName;

    @Schema(description = "种植历史", example = "去年种植过西红柿")
    private String plantingHistory;

    @Schema(description = "环境条件", example = "光照充足，通风良好")
    private String environmentalConditions;

    @Schema(description = "当前种植作物", example = "有机西红柿")
    private String currentCrop;

    @Schema(description = "项目数量", example = "2")
    private Integer projectCount;

    @Schema(description = "单元数量", example = "20")
    private Integer unitCount;

    @Schema(description = "已认养单元数", example = "15")
    private Integer adoptedUnitCount;

    @Schema(description = "认养率", example = "75.0")
    private BigDecimal adoptionRate;

    @Schema(description = "创建时间", example = "2025-01-19T10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-01-19T10:00:00")
    private LocalDateTime updateTime;
}
