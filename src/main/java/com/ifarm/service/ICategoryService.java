package com.ifarm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifarm.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public interface ICategoryService extends IService<Category> {

    /**
     * 根据父分类ID查询子分类列表
     * 
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> getChildrenByParentId(Long parentId);

    /**
     * 根据分类编码查询分类信息
     * 
     * @param code 分类编码
     * @return 分类信息
     */
    Category getCategoryByCode(String code);

    /**
     * 查询启用状态的分类列表
     * 
     * @return 启用的分类列表
     */
    List<Category> getEnabledCategories();

    /**
     * 查询顶级分类列表
     * 
     * @return 顶级分类列表
     */
    List<Category> getTopCategories();

    /**
     * 构建分类树结构
     * 
     * @return 分类树
     */
    List<Category> buildCategoryTree();

    /**
     * 创建分类
     * 
     * @param category 分类信息
     * @return 创建结果
     */
    boolean createCategory(Category category);

    /**
     * 更新分类信息
     * 
     * @param category 分类信息
     * @return 更新结果
     */
    boolean updateCategory(Category category);

    /**
     * 删除分类（检查是否有子分类）
     * 
     * @param categoryId 分类ID
     * @return 删除结果
     */
    boolean deleteCategory(Long categoryId);

    /**
     * 启用/禁用分类
     * 
     * @param categoryId 分类ID
     * @param status 状态：0-禁用，1-启用
     * @return 操作结果
     */
    boolean updateCategoryStatus(Long categoryId, Integer status);

    /**
     * 检查分类编码是否存在
     * 
     * @param code 分类编码
     * @param excludeId 排除的分类ID（用于更新时检查）
     * @return 是否存在
     */
    boolean existsByCode(String code, Long excludeId);

    /**
     * 检查分类是否有子分类
     * 
     * @param categoryId 分类ID
     * @return 是否有子分类
     */
    boolean hasChildren(Long categoryId);

    /**
     * 获取分类的完整路径
     * 
     * @param categoryId 分类ID
     * @return 分类路径（如：蔬菜类 > 叶菜类 > 白菜）
     */
    String getCategoryPath(Long categoryId);
}
