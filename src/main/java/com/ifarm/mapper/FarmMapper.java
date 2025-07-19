package com.ifarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifarm.entity.Farm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 农场Mapper接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Mapper
public interface FarmMapper extends BaseMapper<Farm> {

    /**
     * 根据农场主ID查询农场列表
     * 
     * @param ownerId 农场主ID
     * @return 农场列表
     */
    List<Farm> selectByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * 根据地区查询农场列表
     * 
     * @param province 省份
     * @param city 城市
     * @param district 区县
     * @return 农场列表
     */
    List<Farm> selectByLocation(@Param("province") String province,
                                @Param("city") String city,
                                @Param("district") String district);

    /**
     * 查询正常状态的农场列表
     * 
     * @return 正常状态的农场列表
     */
    List<Farm> selectActiveFarms();

    /**
     * 分页查询农场列表
     * 
     * @param page 分页参数
     * @param ownerId 农场主ID（可选）
     * @param name 农场名称（可选，模糊查询）
     * @param province 省份（可选）
     * @param city 城市（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<Farm> selectFarmPage(Page<Farm> page,
                               @Param("ownerId") Long ownerId,
                               @Param("name") String name,
                               @Param("province") String province,
                               @Param("city") String city,
                               @Param("status") Integer status);

    /**
     * 统计农场主的农场数量
     * 
     * @param ownerId 农场主ID
     * @return 农场数量
     */
    int countByOwnerId(@Param("ownerId") Long ownerId);
}
