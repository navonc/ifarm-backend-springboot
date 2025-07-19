package com.ifarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifarm.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类Mapper接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据父分类ID查询子分类列表
     * 
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 根据分类编码查询分类信息
     * 
     * @param code 分类编码
     * @return 分类信息
     */
    Category selectByCode(@Param("code") String code);

    /**
     * 查询启用状态的分类列表
     * 
     * @return 启用的分类列表
     */
    List<Category> selectEnabledCategories();

    /**
     * 根据父分类ID统计子分类数量
     * 
     * @param parentId 父分类ID
     * @return 子分类数量
     */
    int countByParentId(@Param("parentId") Long parentId);
}
