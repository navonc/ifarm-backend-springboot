package com.ifarm.vo.farm;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 农场VO
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@Schema(description = "农场信息")
public class FarmVO {

    @Schema(description = "农场ID", example = "1")
    private Long id;

    @Schema(description = "农场名称", example = "绿野有机农场")
    private String farmName;

    @Schema(description = "农场主ID", example = "1")
    private Long ownerId;

    @Schema(description = "农场主姓名", example = "张三")
    private String ownerName;

    @Schema(description = "农场地址", example = "北京市昌平区小汤山镇")
    private String address;

    @Schema(description = "纬度", example = "40.2169")
    private BigDecimal latitude;

    @Schema(description = "经度", example = "116.2264")
    private BigDecimal longitude;

    @Schema(description = "农场面积（亩）", example = "100.5")
    private BigDecimal area;

    @Schema(description = "农场描述", example = "专业有机农场，采用生态种植方式")
    private String description;

    @Schema(description = "联系电话", example = "13800138000")
    private String contactPhone;

    @Schema(description = "联系邮箱", example = "farm@example.com")
    private String contactEmail;

    @Schema(description = "农场图片", example = "farm-image.jpg")
    private String farmImage;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

    @Schema(description = "认证信息", example = "有机认证证书编号：ORG2024001")
    private String certificationInfo;

    @Schema(description = "经营范围", example = "有机蔬菜种植、水果种植")
    private String businessScope;

    @Schema(description = "地块数量", example = "10")
    private Integer plotCount;

    @Schema(description = "项目数量", example = "5")
    private Integer projectCount;

    @Schema(description = "总认养数量", example = "200")
    private Integer totalAdoptionCount;

    @Schema(description = "农场评分", example = "4.8")
    private BigDecimal rating;

    @Schema(description = "评价数量", example = "50")
    private Integer reviewCount;

    @Schema(description = "创建时间", example = "2025-01-19T10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-01-19T10:00:00")
    private LocalDateTime updateTime;
}
