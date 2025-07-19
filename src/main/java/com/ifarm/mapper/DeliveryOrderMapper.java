package com.ifarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifarm.entity.DeliveryOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 配送订单Mapper接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Mapper
public interface DeliveryOrderMapper extends BaseMapper<DeliveryOrder> {

    /**
     * 根据用户ID查询配送订单列表
     * 
     * @param userId 用户ID
     * @return 配送订单列表
     */
    List<DeliveryOrder> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据认养记录ID查询配送订单
     * 
     * @param adoptionRecordId 认养记录ID
     * @return 配送订单
     */
    DeliveryOrder selectByAdoptionRecordId(@Param("adoptionRecordId") Long adoptionRecordId);

    /**
     * 根据配送单号查询配送订单
     * 
     * @param orderNo 配送单号
     * @return 配送订单
     */
    DeliveryOrder selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据物流单号查询配送订单
     * 
     * @param trackingNumber 物流单号
     * @return 配送订单
     */
    DeliveryOrder selectByTrackingNumber(@Param("trackingNumber") String trackingNumber);

    /**
     * 根据配送状态查询订单列表
     * 
     * @param deliveryStatus 配送状态
     * @return 配送订单列表
     */
    List<DeliveryOrder> selectByDeliveryStatus(@Param("deliveryStatus") Integer deliveryStatus);

    /**
     * 分页查询用户配送订单
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param deliveryStatus 配送状态（可选）
     * @return 分页结果
     */
    IPage<DeliveryOrder> selectUserDeliveryPage(Page<DeliveryOrder> page,
                                                @Param("userId") Long userId,
                                                @Param("deliveryStatus") Integer deliveryStatus);

    /**
     * 根据物流公司查询配送订单列表
     * 
     * @param logisticsCompany 物流公司
     * @return 配送订单列表
     */
    List<DeliveryOrder> selectByLogisticsCompany(@Param("logisticsCompany") String logisticsCompany);

    /**
     * 统计用户配送订单数量
     * 
     * @param userId 用户ID
     * @param deliveryStatus 配送状态（可选）
     * @return 订单数量
     */
    int countUserDeliveries(@Param("userId") Long userId, @Param("deliveryStatus") Integer deliveryStatus);

    /**
     * 查询配送订单详情（包含认养记录信息）
     * 
     * @param orderId 订单ID
     * @return 配送订单详情
     */
    DeliveryOrder selectDeliveryDetail(@Param("orderId") Long orderId);

    /**
     * 根据配送类型查询订单列表
     * 
     * @param deliveryType 配送类型
     * @return 配送订单列表
     */
    List<DeliveryOrder> selectByDeliveryType(@Param("deliveryType") Integer deliveryType);
}
