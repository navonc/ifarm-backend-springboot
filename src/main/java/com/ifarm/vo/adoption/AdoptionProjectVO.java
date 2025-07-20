package com.ifarm.vo.adoption;

import com.ifarm.vo.crop.CropVO;
import com.ifarm.vo.farm.FarmVO;
import com.ifarm.vo.farmplot.FarmPlotVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 认养项目视图对象
 * 
 * @author ifarm
 * @since 2025-01-20
 */
@Data
@Schema(name = "AdoptionProjectVO", description = "认养项目信息")
public class AdoptionProjectVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID", example = "1")
    private Long id;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称", example = "有机蔬菜认养项目")
    private String name;

    /**
     * 项目描述
     */
    @Schema(description = "项目描述", example = "采用有机种植方式，无农药无化肥，绿色健康")
    private String description;

    /**
     * 农场信息
     */
    @Schema(description = "农场信息")
    private FarmVO farm;

    /**
     * 地块信息
     */
    @Schema(description = "地块信息")
    private FarmPlotVO plot;

    /**
     * 作物信息
     */
    @Schema(description = "作物信息")
    private CropVO crop;

    /**
     * 认养价格（每单元）
     */
    @Schema(description = "认养价格（每单元）", example = "299.00")
    private BigDecimal adoptionPrice;

    /**
     * 总单元数
     */
    @Schema(description = "总单元数", example = "100")
    private Integer totalUnits;

    /**
     * 已认养单元数
     */
    @Schema(description = "已认养单元数", example = "35")
    private Integer adoptedUnits;

    /**
     * 可用单元数
     */
    @Schema(description = "可用单元数", example = "65")
    private Integer availableUnits;

    /**
     * 每单元面积（平方米）
     */
    @Schema(description = "每单元面积（平方米）", example = "10.00")
    private BigDecimal unitArea;

    /**
     * 预计产量（每单元，公斤）
     */
    @Schema(description = "预计产量（每单元，公斤）", example = "50.00")
    private BigDecimal expectedYield;

    /**
     * 认养周期（天）
     */
    @Schema(description = "认养周期（天）", example = "120")
    private Integer adoptionDays;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间", example = "2025-02-01T00:00:00")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间", example = "2025-06-01T00:00:00")
    private LocalDateTime endTime;

    /**
     * 项目状态
     * 1-草稿 2-认养中 3-已满员 4-进行中 5-已完成 6-已取消
     */
    @Schema(description = "项目状态", example = "2")
    private Integer projectStatus;

    /**
     * 项目状态描述
     */
    @Schema(description = "项目状态描述", example = "认养中")
    private String projectStatusText;

    /**
     * 项目图片URL列表
     */
    @Schema(description = "项目图片URL列表")
    private List<String> images;

    /**
     * 项目特色标签
     */
    @Schema(description = "项目特色标签")
    private List<String> tags;

    /**
     * 认养进度百分比
     */
    @Schema(description = "认养进度百分比", example = "35.0")
    private BigDecimal adoptionProgress;

    /**
     * 是否可以认养
     */
    @Schema(description = "是否可以认养", example = "true")
    private Boolean canAdopt;

    /**
     * 剩余认养时间（天）
     */
    @Schema(description = "剩余认养时间（天）", example = "15")
    private Long remainingDays;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "特别注意事项或说明")
    private String remark;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2025-01-20T10:00:00")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2025-01-20T15:30:00")
    private LocalDateTime updateTime;
}
