package com.ifarm.vo.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类VO
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Data
@Schema(description = "分类信息")
public class CategoryVO {

    @Schema(description = "分类ID", example = "1")
    private Long id;

    @Schema(description = "分类名称", example = "蔬菜类")
    private String categoryName;

    @Schema(description = "父分类ID", example = "0")
    private Long parentId;

    @Schema(description = "父分类名称", example = "根分类")
    private String parentName;

    @Schema(description = "分类描述", example = "各种新鲜蔬菜")
    private String description;

    @Schema(description = "分类图标", example = "vegetable-icon.png")
    private String icon;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

    @Schema(description = "分类层级", example = "1")
    private Integer level;

    @Schema(description = "分类路径", example = "0,1")
    private String categoryPath;

    @Schema(description = "子分类数量", example = "5")
    private Integer childrenCount;

    @Schema(description = "作物数量", example = "10")
    private Integer cropCount;

    @Schema(description = "子分类列表")
    private List<CategoryVO> children;

    @Schema(description = "创建时间", example = "2025-01-19T10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-01-19T10:00:00")
    private LocalDateTime updateTime;
}
