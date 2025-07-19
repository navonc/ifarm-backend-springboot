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
 * 配送订单实体类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("delivery_orders")
@Schema(name = "DeliveryOrder", description = "配送订单信息")
public class DeliveryOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配送订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "配送订单ID", example = "1")
    private Long id;

    /**
     * 配送单号
     */
    @TableField("order_no")
    @Schema(description = "配送单号", example = "DL20240601123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderNo;

    /**
     * 认养记录ID
     */
    @TableField("adoption_record_id")
    @Schema(description = "认养记录ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long adoptionRecordId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    /**
     * 收货地址ID
     */
    @TableField("address_id")
    @Schema(description = "收货地址ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long addressId;

    /**
     * 收货人姓名
     */
    @TableField("contact_name")
    @Schema(description = "收货人姓名", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contactName;

    /**
     * 收货人电话
     */
    @TableField("contact_phone")
    @Schema(description = "收货人电话", example = "13800138000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contactPhone;

    /**
     * 收货地址
     */
    @TableField("delivery_address")
    @Schema(description = "收货地址", example = "北京市朝阳区三里屯街道某某小区1号楼101室", requiredMode = Schema.RequiredMode.REQUIRED)
    private String deliveryAddress;

    /**
     * 商品名称
     */
    @TableField("product_name")
    @Schema(description = "商品名称", example = "有机番茄", requiredMode = Schema.RequiredMode.REQUIRED)
    private String productName;

    /**
     * 商品数量（kg）
     */
    @TableField("product_quantity")
    @Schema(description = "商品数量", example = "5.20", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal productQuantity;

    /**
     * 包裹数量
     */
    @TableField("package_count")
    @Schema(description = "包裹数量", example = "1")
    private Integer packageCount;

    /**
     * 配送类型：1-普通配送，2-冷链配送，3-特殊配送
     */
    @TableField("delivery_type")
    @Schema(description = "配送类型", example = "2", allowableValues = {"1", "2", "3"})
    private Integer deliveryType;

    /**
     * 配送费用
     */
    @TableField("delivery_fee")
    @Schema(description = "配送费用", example = "15.00")
    private BigDecimal deliveryFee;

    /**
     * 配送状态：1-待发货，2-已发货，3-运输中，4-派送中，5-已签收，6-配送异常
     */
    @TableField("delivery_status")
    @Schema(description = "配送状态", example = "1", allowableValues = {"1", "2", "3", "4", "5", "6"})
    private Integer deliveryStatus;

    /**
     * 物流公司
     */
    @TableField("logistics_company")
    @Schema(description = "物流公司", example = "顺丰速运")
    private String logisticsCompany;

    /**
     * 物流单号
     */
    @TableField("tracking_number")
    @Schema(description = "物流单号", example = "SF1234567890123")
    private String trackingNumber;

    /**
     * 发货时间
     */
    @TableField("shipped_time")
    @Schema(description = "发货时间", example = "2024-06-01 14:00:00")
    private LocalDateTime shippedTime;

    /**
     * 签收时间
     */
    @TableField("delivered_time")
    @Schema(description = "签收时间", example = "2024-06-02 10:30:00")
    private LocalDateTime deliveredTime;

    /**
     * 预计送达时间
     */
    @TableField("estimated_delivery_time")
    @Schema(description = "预计送达时间", example = "2024-06-02 18:00:00")
    private LocalDateTime estimatedDeliveryTime;

    /**
     * 配送备注
     */
    @TableField("delivery_notes")
    @Schema(description = "配送备注", example = "请保持冷链运输，确保新鲜度")
    private String deliveryNotes;

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
