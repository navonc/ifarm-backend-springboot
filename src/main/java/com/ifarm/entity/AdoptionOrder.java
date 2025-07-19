package com.ifarm.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 认养订单实体类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("adoption_orders")
@Schema(name = "AdoptionOrder", description = "认养订单信息")
public class AdoptionOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "订单ID", example = "1")
    private Long id;

    /**
     * 订单号
     */
    @TableField("order_no")
    @Schema(description = "订单号", example = "AD20240101123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderNo;

    /**
     * 用户ID
     */
    @TableField("user_id")
    @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    /**
     * 项目ID
     */
    @TableField("project_id")
    @Schema(description = "项目ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long projectId;

    /**
     * 认养单元数量
     */
    @TableField("unit_count")
    @Schema(description = "认养单元数量", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer unitCount;

    /**
     * 单元价格
     */
    @TableField("unit_price")
    @Schema(description = "单元价格", example = "299.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal unitPrice;

    /**
     * 订单总金额
     */
    @TableField("total_amount")
    @Schema(description = "订单总金额", example = "598.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    @TableField("discount_amount")
    @Schema(description = "优惠金额", example = "0.00")
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    @TableField("actual_amount")
    @Schema(description = "实付金额", example = "598.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal actualAmount;

    /**
     * 订单状态：1-待支付，2-已支付，3-已完成，4-已取消，5-已退款
     */
    @TableField("order_status")
    @Schema(description = "订单状态", example = "1", allowableValues = {"1", "2", "3", "4", "5"})
    private Integer orderStatus;

    /**
     * 支付方式：wechat,alipay
     */
    @TableField("payment_method")
    @Schema(description = "支付方式", example = "wechat", allowableValues = {"wechat", "alipay"})
    private String paymentMethod;

    /**
     * 支付时间
     */
    @TableField("payment_time")
    @Schema(description = "支付时间", example = "2024-01-01 12:30:00")
    private LocalDateTime paymentTime;

    /**
     * 支付流水号
     */
    @TableField("payment_no")
    @Schema(description = "支付流水号", example = "wx20240101123456789")
    private String paymentNo;

    /**
     * 订单备注
     */
    @TableField("remark")
    @Schema(description = "订单备注", example = "希望种植有机番茄")
    private String remark;

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
