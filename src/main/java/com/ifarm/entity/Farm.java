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
 * 农场实体类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("farms")
@Schema(name = "Farm", description = "农场信息")
public class Farm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 农场ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "农场ID", example = "1")
    private Long id;

    /**
     * 农场主用户ID
     */
    @TableField("owner_id")
    @Schema(description = "农场主用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long ownerId;

    /**
     * 农场名称
     */
    @TableField("name")
    @Schema(description = "农场名称", example = "绿野仙踪有机农场", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 农场描述
     */
    @TableField("description")
    @Schema(description = "农场描述", example = "专注有机种植，绿色健康生活")
    private String description;

    /**
     * 省份
     */
    @TableField("province")
    @Schema(description = "省份", example = "北京市", requiredMode = Schema.RequiredMode.REQUIRED)
    private String province;

    /**
     * 城市
     */
    @TableField("city")
    @Schema(description = "城市", example = "北京市", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    /**
     * 区县
     */
    @TableField("district")
    @Schema(description = "区县", example = "昌平区", requiredMode = Schema.RequiredMode.REQUIRED)
    private String district;

    /**
     * 详细地址
     */
    @TableField("address")
    @Schema(description = "详细地址", example = "小汤山镇农业园区88号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;

    /**
     * 纬度
     */
    @TableField("latitude")
    @Schema(description = "纬度", example = "40.123456")
    private BigDecimal latitude;

    /**
     * 经度
     */
    @TableField("longitude")
    @Schema(description = "经度", example = "116.123456")
    private BigDecimal longitude;

    /**
     * 总面积（亩）
     */
    @TableField("total_area")
    @Schema(description = "总面积", example = "100.50")
    private BigDecimal totalArea;

    /**
     * 封面图片
     */
    @TableField("cover_image")
    @Schema(description = "封面图片", example = "/images/farms/farm_cover.jpg")
    private String coverImage;

    /**
     * 农场图片（JSON数组）
     */
    @TableField("images")
    @Schema(description = "农场图片", example = "[\"/images/farms/farm1.jpg\", \"/images/farms/farm2.jpg\"]")
    private String images;

    /**
     * 营业执照号
     */
    @TableField("license_number")
    @Schema(description = "营业执照号", example = "110000000000001")
    private String licenseNumber;

    /**
     * 认证信息（JSON格式）
     */
    @TableField("certification")
    @Schema(description = "认证信息", example = "{\"organic\": true, \"green\": true}")
    private String certification;

    /**
     * 联系电话
     */
    @TableField("contact_phone")
    @Schema(description = "联系电话", example = "13800138000")
    private String contactPhone;

    /**
     * 营业时间
     */
    @TableField("business_hours")
    @Schema(description = "营业时间", example = "08:00-18:00")
    private String businessHours;

    /**
     * 状态：0-禁用，1-正常，2-审核中
     */
    @TableField("status")
    @Schema(description = "状态", example = "1", allowableValues = {"0", "1", "2"})
    private Integer status;

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
