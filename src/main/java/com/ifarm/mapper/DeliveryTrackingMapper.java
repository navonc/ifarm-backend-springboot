package com.ifarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifarm.entity.DeliveryTracking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物流跟踪Mapper接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Mapper
public interface DeliveryTrackingMapper extends BaseMapper<DeliveryTracking> {

    /**
     * 根据配送订单ID查询跟踪记录列表
     * 
     * @param deliveryOrderId 配送订单ID
     * @return 跟踪记录列表
     */
    List<DeliveryTracking> selectByDeliveryOrderId(@Param("deliveryOrderId") Long deliveryOrderId);

    /**
     * 根据配送订单ID查询最新跟踪记录
     * 
     * @param deliveryOrderId 配送订单ID
     * @return 最新跟踪记录
     */
    DeliveryTracking selectLatestByDeliveryOrderId(@Param("deliveryOrderId") Long deliveryOrderId);

    /**
     * 根据物流状态查询跟踪记录列表
     * 
     * @param trackingStatus 物流状态
     * @return 跟踪记录列表
     */
    List<DeliveryTracking> selectByTrackingStatus(@Param("trackingStatus") String trackingStatus);

    /**
     * 根据操作人查询跟踪记录列表
     * 
     * @param operator 操作人
     * @return 跟踪记录列表
     */
    List<DeliveryTracking> selectByOperator(@Param("operator") String operator);

    /**
     * 统计配送订单的跟踪记录数量
     * 
     * @param deliveryOrderId 配送订单ID
     * @return 记录数量
     */
    int countByDeliveryOrderId(@Param("deliveryOrderId") Long deliveryOrderId);
}
