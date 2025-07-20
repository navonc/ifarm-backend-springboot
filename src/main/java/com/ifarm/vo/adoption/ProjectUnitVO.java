package com.ifarm.vo.adoption;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 项目单元视图对象
 * 
 * @author ifarm
 * @since 2025-01-20
 */
@Data
@Schema(name = "ProjectUnitVO", description = "项目单元信息")
public class ProjectUnitVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 单元ID
     */
    @Schema(description = "单元ID", example = "1")
    private Long id;

    /**
     * 认养项目ID
     */
    @Schema(description = "认养项目ID", example = "1")
    private Long projectId;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称", example = "有机蔬菜认养项目")
    private String projectName;

    /**
     * 单元编号
     */
    @Schema(description = "单元编号", example = "P1-001")
    private String unitNumber;

    /**
     * 单元面积（平方米）
     */
    @Schema(description = "单元面积（平方米）", example = "10.00")
    private BigDecimal unitArea;

    /**
     * 位置描述
     */
    @Schema(description = "位置描述", example = "地块东南角，靠近水源")
    private String locationDesc;

    /**
     * 土壤类型
     */
    @Schema(description = "土壤类型", example = "黑土")
    private String soilType;

    /**
     * 单元状态
     * 1-可用 2-已认养 3-维护中 4-已收获
     */
    @Schema(description = "单元状态", example = "1")
    private Integer unitStatus;

    /**
     * 单元状态描述
     */
    @Schema(description = "单元状态描述", example = "可用")
    private String unitStatusText;

    /**
     * 认养用户ID（如果已认养）
     */
    @Schema(description = "认养用户ID", example = "123")
    private Long adopterId;

    /**
     * 认养用户名称（如果已认养）
     */
    @Schema(description = "认养用户名称", example = "张三")
    private String adopterName;

    /**
     * 认养时间（如果已认养）
     */
    @Schema(description = "认养时间", example = "2025-01-20T10:00:00")
    private LocalDateTime adoptionTime;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "特殊说明")
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
