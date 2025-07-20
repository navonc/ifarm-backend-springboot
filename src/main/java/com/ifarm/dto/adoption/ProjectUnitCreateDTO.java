package com.ifarm.dto.adoption;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 项目单元创建DTO
 * 
 * @author ifarm
 * @since 2025-01-20
 */
@Data
@Schema(name = "ProjectUnitCreateDTO", description = "项目单元创建")
public class ProjectUnitCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 认养项目ID
     */
    @NotNull(message = "认养项目ID不能为空")
    @Schema(description = "认养项目ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long projectId;

    /**
     * 单元编号
     */
    @NotBlank(message = "单元编号不能为空")
    @Size(max = 50, message = "单元编号长度不能超过50个字符")
    @Schema(description = "单元编号", example = "P1-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String unitNumber;

    /**
     * 单元面积（平方米）
     */
    @NotNull(message = "单元面积不能为空")
    @DecimalMin(value = "0.01", message = "单元面积必须大于0")
    @Digits(integer = 8, fraction = 2, message = "单元面积格式不正确")
    @Schema(description = "单元面积（平方米）", example = "10.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal unitArea;

    /**
     * 位置描述
     */
    @Size(max = 200, message = "位置描述长度不能超过200个字符")
    @Schema(description = "位置描述", example = "地块东南角，靠近水源")
    private String locationDesc;

    /**
     * 土壤类型
     */
    @Size(max = 50, message = "土壤类型长度不能超过50个字符")
    @Schema(description = "土壤类型", example = "黑土")
    private String soilType;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Schema(description = "备注", example = "特殊说明")
    private String remark;
}
