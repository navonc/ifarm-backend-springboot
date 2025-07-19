package com.ifarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifarm.entity.FarmPlot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 农场地块Mapper接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Mapper
public interface FarmPlotMapper extends BaseMapper<FarmPlot> {

    /**
     * 根据农场ID查询地块列表
     * 
     * @param farmId 农场ID
     * @return 地块列表
     */
    List<FarmPlot> selectByFarmId(@Param("farmId") Long farmId);

    /**
     * 根据农场ID查询可用地块列表
     * 
     * @param farmId 农场ID
     * @return 可用地块列表
     */
    List<FarmPlot> selectAvailableByFarmId(@Param("farmId") Long farmId);

    /**
     * 统计农场的地块数量
     * 
     * @param farmId 农场ID
     * @return 地块数量
     */
    int countByFarmId(@Param("farmId") Long farmId);

    /**
     * 根据土壤类型查询地块列表
     * 
     * @param soilType 土壤类型
     * @return 地块列表
     */
    List<FarmPlot> selectBySoilType(@Param("soilType") String soilType);
}
