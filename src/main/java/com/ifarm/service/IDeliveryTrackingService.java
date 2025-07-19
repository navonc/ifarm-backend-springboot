package com.ifarm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifarm.entity.DeliveryTracking;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 物流跟踪服务接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public interface IDeliveryTrackingService extends IService<DeliveryTracking> {

    /**
     * 根据配送订单ID查询跟踪记录列表
     * 
     * @param deliveryOrderId 配送订单ID
     * @return 跟踪记录列表
     */
    List<DeliveryTracking> getTrackingsByDeliveryOrderId(Long deliveryOrderId);

    /**
     * 根据配送订单ID查询最新跟踪记录
     * 
     * @param deliveryOrderId 配送订单ID
     * @return 最新跟踪记录
     */
    DeliveryTracking getLatestTrackingByDeliveryOrderId(Long deliveryOrderId);

    /**
     * 根据物流状态查询跟踪记录列表
     * 
     * @param trackingStatus 物流状态
     * @return 跟踪记录列表
     */
    List<DeliveryTracking> getTrackingsByStatus(String trackingStatus);

    /**
     * 根据操作人查询跟踪记录列表
     * 
     * @param operator 操作人
     * @return 跟踪记录列表
     */
    List<DeliveryTracking> getTrackingsByOperator(String operator);

    /**
     * 创建跟踪记录
     * 
     * @param deliveryOrderId 配送订单ID
     * @param trackingStatus 物流状态
     * @param trackingInfo 物流信息
     * @param location 当前位置
     * @param operator 操作人
     * @param trackingTime 跟踪时间
     * @return 创建结果
     */
    boolean createTracking(Long deliveryOrderId, String trackingStatus, String trackingInfo, 
                          String location, String operator, LocalDateTime trackingTime);

    /**
     * 批量创建跟踪记录
     * 
     * @param trackingList 跟踪记录列表
     * @return 创建结果
     */
    boolean batchCreateTrackings(List<DeliveryTracking> trackingList);

    /**
     * 更新跟踪记录
     * 
     * @param deliveryTracking 跟踪记录
     * @return 更新结果
     */
    boolean updateTracking(DeliveryTracking deliveryTracking);

    /**
     * 删除跟踪记录
     * 
     * @param trackingId 跟踪记录ID
     * @return 删除结果
     */
    boolean deleteTracking(Long trackingId);

    /**
     * 获取跟踪记录详情
     * 
     * @param trackingId 跟踪记录ID
     * @return 跟踪记录详情
     */
    DeliveryTracking getTrackingDetail(Long trackingId);

    /**
     * 统计配送订单的跟踪记录数量
     * 
     * @param deliveryOrderId 配送订单ID
     * @return 记录数量
     */
    int countTrackingsByDeliveryOrderId(Long deliveryOrderId);

    /**
     * 获取物流跟踪时间线
     * 
     * @param deliveryOrderId 配送订单ID
     * @return 跟踪时间线
     */
    List<DeliveryTracking> getTrackingTimeline(Long deliveryOrderId);

    /**
     * 添加发货跟踪记录
     * 
     * @param deliveryOrderId 配送订单ID
     * @param location 发货地点
     * @param operator 操作人
     * @return 添加结果
     */
    boolean addShipmentTracking(Long deliveryOrderId, String location, String operator);

    /**
     * 添加运输中跟踪记录
     * 
     * @param deliveryOrderId 配送订单ID
     * @param location 当前位置
     * @param trackingInfo 物流信息
     * @return 添加结果
     */
    boolean addInTransitTracking(Long deliveryOrderId, String location, String trackingInfo);

    /**
     * 添加派送中跟踪记录
     * 
     * @param deliveryOrderId 配送订单ID
     * @param location 派送地点
     * @param operator 派送员
     * @return 添加结果
     */
    boolean addOutForDeliveryTracking(Long deliveryOrderId, String location, String operator);

    /**
     * 添加签收跟踪记录
     * 
     * @param deliveryOrderId 配送订单ID
     * @param location 签收地点
     * @param operator 签收人
     * @return 添加结果
     */
    boolean addDeliveredTracking(Long deliveryOrderId, String location, String operator);

    /**
     * 添加异常跟踪记录
     * 
     * @param deliveryOrderId 配送订单ID
     * @param exceptionInfo 异常信息
     * @param location 异常地点
     * @param operator 处理人
     * @return 添加结果
     */
    boolean addExceptionTracking(Long deliveryOrderId, String exceptionInfo, String location, String operator);

    /**
     * 从第三方物流API同步跟踪信息
     * 
     * @param deliveryOrderId 配送订单ID
     * @param trackingNumber 物流单号
     * @param logisticsCompany 物流公司
     * @return 同步结果
     */
    boolean syncTrackingFromAPI(Long deliveryOrderId, String trackingNumber, String logisticsCompany);

    /**
     * 获取物流状态列表
     * 
     * @return 物流状态列表
     */
    List<String> getTrackingStatuses();

    /**
     * 检查是否有新的跟踪更新
     * 
     * @param deliveryOrderId 配送订单ID
     * @param lastCheckTime 上次检查时间
     * @return 是否有更新
     */
    boolean hasNewTrackingUpdates(Long deliveryOrderId, LocalDateTime lastCheckTime);

    /**
     * 获取用户的物流跟踪信息
     * 
     * @param userId 用户ID
     * @param deliveryOrderId 配送订单ID
     * @return 跟踪信息列表
     */
    List<DeliveryTracking> getUserTrackingInfo(Long userId, Long deliveryOrderId);

    /**
     * 格式化跟踪信息用于显示
     * 
     * @param deliveryOrderId 配送订单ID
     * @return 格式化的跟踪信息
     */
    Object formatTrackingInfo(Long deliveryOrderId);

    /**
     * 预测配送时间
     * 
     * @param deliveryOrderId 配送订单ID
     * @return 预测送达时间
     */
    LocalDateTime predictDeliveryTime(Long deliveryOrderId);
}
