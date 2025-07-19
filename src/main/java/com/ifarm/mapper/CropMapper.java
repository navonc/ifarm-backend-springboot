package com.ifarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifarm.entity.Crop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作物品种Mapper接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Mapper
public interface CropMapper extends BaseMapper<Crop> {

    /**
     * 根据分类ID查询作物列表
     * 
     * @param categoryId 分类ID
     * @return 作物列表
     */
    List<Crop> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 查询启用状态的作物列表
     * 
     * @return 启用的作物列表
     */
    List<Crop> selectEnabledCrops();

    /**
     * 分页查询作物列表
     * 
     * @param page 分页参数
     * @param categoryId 分类ID（可选）
     * @param name 作物名称（可选，模糊查询）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<Crop> selectCropPage(Page<Crop> page, 
                               @Param("categoryId") Long categoryId,
                               @Param("name") String name,
                               @Param("status") Integer status);

    /**
     * 根据种植季节查询作物列表
     * 
     * @param plantingSeason 种植季节
     * @return 作物列表
     */
    List<Crop> selectByPlantingSeason(@Param("plantingSeason") String plantingSeason);
}
