package com.ifarm.dto.farm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 农场创建DTO
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@Schema(description = "农场创建请求")
public class FarmCreateDTO {

    @NotBlank(message = "农场名称不能为空")
    @Size(max = 100, message = "农场名称长度不能超过100个字符")
    @Schema(description = "农场名称", example = "绿野有机农场")
    private String farmName;

    @NotNull(message = "农场主ID不能为空")
    @Schema(description = "农场主ID", example = "1")
    private Long ownerId;

    @NotBlank(message = "农场地址不能为空")
    @Size(max = 200, message = "农场地址长度不能超过200个字符")
    @Schema(description = "农场地址", example = "北京市昌平区小汤山镇")
    private String address;

    @DecimalMin(value = "-90.0", message = "纬度必须在-90到90之间")
    @DecimalMax(value = "90.0", message = "纬度必须在-90到90之间")
    @Schema(description = "纬度", example = "40.2169")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "经度必须在-180到180之间")
    @DecimalMax(value = "180.0", message = "经度必须在-180到180之间")
    @Schema(description = "经度", example = "116.2264")
    private BigDecimal longitude;

    @Positive(message = "农场面积必须为正数")
    @Schema(description = "农场面积（亩）", example = "100.5")
    private BigDecimal area;

    @Size(max = 500, message = "农场描述长度不能超过500个字符")
    @Schema(description = "农场描述", example = "专业有机农场，采用生态种植方式")
    private String description;

    @Size(max = 100, message = "联系电话长度不能超过100个字符")
    @Schema(description = "联系电话", example = "13800138000")
    private String contactPhone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    @Schema(description = "联系邮箱", example = "farm@example.com")
    private String contactEmail;

    @Schema(description = "农场图片", example = "farm-image.jpg")
    private String farmImage;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled = true;

    @Size(max = 200, message = "认证信息长度不能超过200个字符")
    @Schema(description = "认证信息", example = "有机认证证书编号：ORG2024001")
    private String certificationInfo;

    @Size(max = 100, message = "经营范围长度不能超过100个字符")
    @Schema(description = "经营范围", example = "有机蔬菜种植、水果种植")
    private String businessScope;
}
