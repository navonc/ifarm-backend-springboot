package com.ifarm.dto.crop;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 作物更新DTO
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@Schema(description = "作物更新请求")
public class CropUpdateDTO {

    @Size(max = 50, message = "作物名称长度不能超过50个字符")
    @Schema(description = "作物名称", example = "有机西红柿")
    private String cropName;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Size(max = 100, message = "品种长度不能超过100个字符")
    @Schema(description = "作物品种", example = "樱桃番茄")
    private String variety;

    @Size(max = 500, message = "作物描述长度不能超过500个字符")
    @Schema(description = "作物描述", example = "优质有机西红柿，口感鲜美，营养丰富")
    private String description;

    @Positive(message = "生长周期必须为正数")
    @Schema(description = "生长周期（天）", example = "90")
    private Integer growthCycle;

    @Size(max = 50, message = "种植季节长度不能超过50个字符")
    @Schema(description = "种植季节", example = "春季")
    private String plantingSeason;

    @Size(max = 50, message = "收获季节长度不能超过50个字符")
    @Schema(description = "收获季节", example = "夏季")
    private String harvestSeason;

    @Schema(description = "作物图片", example = "tomato.jpg")
    private String cropImage;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

    @Size(max = 200, message = "种植要求长度不能超过200个字符")
    @Schema(description = "种植要求", example = "需要充足阳光和适量水分")
    private String plantingRequirements;

    @Size(max = 200, message = "营养价值长度不能超过200个字符")
    @Schema(description = "营养价值", example = "富含维生素C和番茄红素")
    private String nutritionalValue;
}
