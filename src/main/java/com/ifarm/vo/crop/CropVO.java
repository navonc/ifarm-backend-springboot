package com.ifarm.vo.crop;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 作物VO
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@Schema(description = "作物信息")
public class CropVO {

    @Schema(description = "作物ID", example = "1")
    private Long id;

    @Schema(description = "作物名称", example = "有机西红柿")
    private String cropName;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "蔬菜类")
    private String categoryName;

    @Schema(description = "作物品种", example = "樱桃番茄")
    private String variety;

    @Schema(description = "作物描述", example = "优质有机西红柿，口感鲜美，营养丰富")
    private String description;

    @Schema(description = "生长周期（天）", example = "90")
    private Integer growthCycle;

    @Schema(description = "种植季节", example = "春季")
    private String plantingSeason;

    @Schema(description = "收获季节", example = "夏季")
    private String harvestSeason;

    @Schema(description = "作物图片", example = "tomato.jpg")
    private String cropImage;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

    @Schema(description = "种植要求", example = "需要充足阳光和适量水分")
    private String plantingRequirements;

    @Schema(description = "营养价值", example = "富含维生素C和番茄红素")
    private String nutritionalValue;

    @Schema(description = "项目数量", example = "5")
    private Integer projectCount;

    @Schema(description = "总认养数量", example = "100")
    private Integer totalAdoptionCount;

    @Schema(description = "创建时间", example = "2025-01-19T10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-01-19T10:00:00")
    private LocalDateTime updateTime;
}
