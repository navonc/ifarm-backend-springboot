package com.ifarm.dto.adoption;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 认养项目创建DTO
 * 
 * @author ifarm
 * @since 2025-01-20
 */
@Data
@Schema(name = "AdoptionProjectCreateDTO", description = "认养项目创建")
public class AdoptionProjectCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 100, message = "项目名称长度不能超过100个字符")
    @Schema(description = "项目名称", example = "有机蔬菜认养项目", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 项目描述
     */
    @Size(max = 1000, message = "项目描述长度不能超过1000个字符")
    @Schema(description = "项目描述", example = "采用有机种植方式，无农药无化肥，绿色健康")
    private String description;

    /**
     * 农场ID
     */
    @NotNull(message = "农场ID不能为空")
    @Schema(description = "农场ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long farmId;

    /**
     * 地块ID
     */
    @NotNull(message = "地块ID不能为空")
    @Schema(description = "地块ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long plotId;

    /**
     * 作物ID
     */
    @NotNull(message = "作物ID不能为空")
    @Schema(description = "作物ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long cropId;

    /**
     * 认养价格（每单元）
     */
    @NotNull(message = "认养价格不能为空")
    @DecimalMin(value = "0.01", message = "认养价格必须大于0")
    @Digits(integer = 8, fraction = 2, message = "认养价格格式不正确")
    @Schema(description = "认养价格（每单元）", example = "299.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal adoptionPrice;

    /**
     * 总单元数
     */
    @NotNull(message = "总单元数不能为空")
    @Min(value = 1, message = "总单元数必须大于0")
    @Schema(description = "总单元数", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalUnits;

    /**
     * 每单元面积（平方米）
     */
    @NotNull(message = "每单元面积不能为空")
    @DecimalMin(value = "0.01", message = "每单元面积必须大于0")
    @Digits(integer = 8, fraction = 2, message = "每单元面积格式不正确")
    @Schema(description = "每单元面积（平方米）", example = "10.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal unitArea;

    /**
     * 预计产量（每单元，公斤）
     */
    @DecimalMin(value = "0", message = "预计产量不能为负数")
    @Digits(integer = 8, fraction = 2, message = "预计产量格式不正确")
    @Schema(description = "预计产量（每单元，公斤）", example = "50.00")
    private BigDecimal expectedYield;

    /**
     * 认养周期（天）
     */
    @NotNull(message = "认养周期不能为空")
    @Min(value = 1, message = "认养周期必须大于0天")
    @Schema(description = "认养周期（天）", example = "120", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer adoptionDays;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @Future(message = "开始时间必须是未来时间")
    @Schema(description = "开始时间", example = "2025-02-01T00:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @Future(message = "结束时间必须是未来时间")
    @Schema(description = "结束时间", example = "2025-06-01T00:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime endTime;

    /**
     * 项目图片URL列表（JSON格式）
     */
    @Schema(description = "项目图片URL列表", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
    private String images;

    /**
     * 项目特色标签（JSON格式）
     */
    @Schema(description = "项目特色标签", example = "[\"有机认证\", \"无农药\", \"现摘现发\"]")
    private String tags;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Schema(description = "备注", example = "特别注意事项或说明")
    private String remark;
}
