package com.ifarm.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 分类创建DTO
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@Schema(description = "分类创建请求")
public class CategoryCreateDTO {

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    @Schema(description = "分类名称", example = "蔬菜类")
    private String categoryName;

    @Schema(description = "父分类ID", example = "1")
    private Long parentId;

    @Size(max = 200, message = "分类描述长度不能超过200个字符")
    @Schema(description = "分类描述", example = "各种新鲜蔬菜")
    private String description;

    @Schema(description = "分类图标", example = "vegetable-icon.png")
    private String icon;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled = true;
}
