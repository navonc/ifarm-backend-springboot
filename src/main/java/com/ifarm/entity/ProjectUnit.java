package com.ifarm.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 项目单元实体类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("project_units")
@Schema(name = "ProjectUnit", description = "项目单元信息")
public class ProjectUnit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 单元ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "单元ID", example = "1")
    private Long id;

    /**
     * 项目ID
     */
    @TableField("project_id")
    @Schema(description = "项目ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long projectId;

    /**
     * 单元编号
     */
    @TableField("unit_number")
    @Schema(description = "单元编号", example = "A001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String unitNumber;

    /**
     * 单元状态：1-可认养，2-已认养，3-种植中，4-待收获，5-已收获
     */
    @TableField("unit_status")
    @Schema(description = "单元状态", example = "1", allowableValues = {"1", "2", "3", "4", "5"})
    private Integer unitStatus;

    /**
     * 单元位置信息（JSON格式）
     */
    @TableField("location_info")
    @Schema(description = "单元位置信息", example = "{\"row\": 1, \"column\": 1, \"coordinates\": \"A1\"}")
    private String locationInfo;

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
