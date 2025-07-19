package com.ifarm.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户收货地址实体类
 * 
 * @author ifarm
 * @since 2025-7-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_addresses")
@Schema(name = "UserAddress", description = "用户收货地址信息")
public class UserAddress implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 地址ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "地址ID", example = "1")
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    /**
     * 联系人姓名
     */
    @TableField("contact_name")
    @Schema(description = "联系人姓名", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contactName;

    /**
     * 联系电话
     */
    @TableField("contact_phone")
    @Schema(description = "联系电话", example = "13800138000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contactPhone;

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
    @Schema(description = "区县", example = "朝阳区", requiredMode = Schema.RequiredMode.REQUIRED)
    private String district;

    /**
     * 详细地址
     */
    @TableField("detail_address")
    @Schema(description = "详细地址", example = "三里屯街道某某小区1号楼101室", requiredMode = Schema.RequiredMode.REQUIRED)
    private String detailAddress;

    /**
     * 邮政编码
     */
    @TableField("postal_code")
    @Schema(description = "邮政编码", example = "100000")
    private String postalCode;

    /**
     * 是否默认地址：0-否，1-是
     */
    @TableField("is_default")
    @Schema(description = "是否默认地址", example = "1", allowableValues = {"0", "1"})
    private Integer isDefault;

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
