package com.ifarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifarm.entity.AdoptionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户认养记录Mapper接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Mapper
public interface AdoptionRecordMapper extends BaseMapper<AdoptionRecord> {

    /**
     * 根据用户ID查询认养记录列表
     * 
     * @param userId 用户ID
     * @return 认养记录列表
     */
    List<AdoptionRecord> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据项目ID查询认养记录列表
     * 
     * @param projectId 项目ID
     * @return 认养记录列表
     */
    List<AdoptionRecord> selectByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据订单ID查询认养记录列表
     * 
     * @param orderId 订单ID
     * @return 认养记录列表
     */
    List<AdoptionRecord> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据单元ID查询认养记录
     * 
     * @param unitId 单元ID
     * @return 认养记录
     */
    AdoptionRecord selectByUnitId(@Param("unitId") Long unitId);

    /**
     * 分页查询用户认养记录
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param adoptionStatus 认养状态（可选）
     * @return 分页结果
     */
    IPage<AdoptionRecord> selectUserAdoptionPage(Page<AdoptionRecord> page,
                                                 @Param("userId") Long userId,
                                                 @Param("adoptionStatus") Integer adoptionStatus);

    /**
     * 根据认养状态查询记录列表
     * 
     * @param adoptionStatus 认养状态
     * @return 认养记录列表
     */
    List<AdoptionRecord> selectByAdoptionStatus(@Param("adoptionStatus") Integer adoptionStatus);

    /**
     * 统计用户认养记录数量
     * 
     * @param userId 用户ID
     * @param adoptionStatus 认养状态（可选）
     * @return 记录数量
     */
    int countUserAdoptions(@Param("userId") Long userId, @Param("adoptionStatus") Integer adoptionStatus);

    /**
     * 查询认养记录详情（包含项目、作物、农场信息）
     * 
     * @param recordId 记录ID
     * @return 认养记录详情
     */
    AdoptionRecord selectRecordDetail(@Param("recordId") Long recordId);

    /**
     * 批量更新认养状态
     * 
     * @param recordIds 记录ID列表
     * @param adoptionStatus 新状态
     * @return 更新行数
     */
    int batchUpdateAdoptionStatus(@Param("recordIds") List<Long> recordIds, 
                                  @Param("adoptionStatus") Integer adoptionStatus);
}
