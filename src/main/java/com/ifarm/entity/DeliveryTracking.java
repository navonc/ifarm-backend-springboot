package com.ifarm.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 物流跟踪实体类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("delivery_tracking")
@Schema(name = "DeliveryTracking", description = "物流跟踪信息")
public class DeliveryTracking implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 跟踪记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "跟踪记录ID", example = "1")
    private Long id;

    /**
     * 配送订单ID
     */
    @TableField("delivery_order_id")
    @Schema(description = "配送订单ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long deliveryOrderId;

    /**
     * 物流状态
     */
    @TableField("tracking_status")
    @Schema(description = "物流状态", example = "已发货", requiredMode = Schema.RequiredMode.REQUIRED)
    private String trackingStatus;

    /**
     * 物流信息
     */
    @TableField("tracking_info")
    @Schema(description = "物流信息", example = "您的包裹已从北京分拣中心发出")
    private String trackingInfo;

    /**
     * 当前位置
     */
    @TableField("location")
    @Schema(description = "当前位置", example = "北京市朝阳区分拣中心")
    private String location;

    /**
     * 操作人
     */
    @TableField("operator")
    @Schema(description = "操作人", example = "张师傅")
    private String operator;

    /**
     * 跟踪时间
     */
    @TableField("tracking_time")
    @Schema(description = "跟踪时间", example = "2024-06-01 14:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime trackingTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createTime;
}
