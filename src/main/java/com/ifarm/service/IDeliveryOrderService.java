package com.ifarm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ifarm.entity.DeliveryOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 配送订单服务接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public interface IDeliveryOrderService extends IService<DeliveryOrder> {

    /**
     * 根据用户ID查询配送订单列表
     * 
     * @param userId 用户ID
     * @return 配送订单列表
     */
    List<DeliveryOrder> getOrdersByUserId(Long userId);

    /**
     * 根据认养记录ID查询配送订单
     * 
     * @param adoptionRecordId 认养记录ID
     * @return 配送订单
     */
    DeliveryOrder getOrderByAdoptionRecordId(Long adoptionRecordId);

    /**
     * 根据配送单号查询配送订单
     * 
     * @param orderNo 配送单号
     * @return 配送订单
     */
    DeliveryOrder getOrderByOrderNo(String orderNo);

    /**
     * 根据物流单号查询配送订单
     * 
     * @param trackingNumber 物流单号
     * @return 配送订单
     */
    DeliveryOrder getOrderByTrackingNumber(String trackingNumber);

    /**
     * 根据配送状态查询订单列表
     * 
     * @param deliveryStatus 配送状态
     * @return 配送订单列表
     */
    List<DeliveryOrder> getOrdersByDeliveryStatus(Integer deliveryStatus);

    /**
     * 分页查询用户配送订单
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param deliveryStatus 配送状态（可选）
     * @return 分页结果
     */
    IPage<DeliveryOrder> getUserDeliveryPage(Page<DeliveryOrder> page, Long userId, Integer deliveryStatus);

    /**
     * 根据物流公司查询配送订单列表
     * 
     * @param logisticsCompany 物流公司
     * @return 配送订单列表
     */
    List<DeliveryOrder> getOrdersByLogisticsCompany(String logisticsCompany);

    /**
     * 创建配送订单
     * 
     * @param adoptionRecordId 认养记录ID
     * @param addressId 收货地址ID
     * @param deliveryType 配送类型
     * @param deliveryNotes 配送备注
     * @return 配送订单
     */
    DeliveryOrder createDeliveryOrder(Long adoptionRecordId, Long addressId, 
                                     Integer deliveryType, String deliveryNotes);

    /**
     * 更新配送订单
     * 
     * @param deliveryOrder 配送订单
     * @return 更新结果
     */
    boolean updateDeliveryOrder(DeliveryOrder deliveryOrder);

    /**
     * 删除配送订单
     * 
     * @param orderId 订单ID
     * @return 删除结果
     */
    boolean deleteDeliveryOrder(Long orderId);

    /**
     * 发货
     * 
     * @param orderId 订单ID
     * @param logisticsCompany 物流公司
     * @param trackingNumber 物流单号
     * @param estimatedDeliveryTime 预计送达时间
     * @return 发货结果
     */
    boolean shipOrder(Long orderId, String logisticsCompany, String trackingNumber, 
                     LocalDateTime estimatedDeliveryTime);

    /**
     * 更新配送状态
     * 
     * @param orderId 订单ID
     * @param deliveryStatus 配送状态
     * @return 更新结果
     */
    boolean updateDeliveryStatus(Long orderId, Integer deliveryStatus);

    /**
     * 确认签收
     * 
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 签收结果
     */
    boolean confirmDelivery(Long orderId, Long userId);

    /**
     * 处理配送异常
     * 
     * @param orderId 订单ID
     * @param exceptionReason 异常原因
     * @param handleMethod 处理方式
     * @return 处理结果
     */
    boolean handleDeliveryException(Long orderId, String exceptionReason, String handleMethod);

    /**
     * 获取配送订单详情（包含认养记录信息）
     * 
     * @param orderId 订单ID
     * @return 配送订单详情
     */
    DeliveryOrder getDeliveryDetail(Long orderId);

    /**
     * 检查用户是否有权限操作订单
     * 
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 是否有权限
     */
    boolean hasPermission(Long userId, Long orderId);

    /**
     * 生成配送单号
     * 
     * @return 配送单号
     */
    String generateOrderNo();

    /**
     * 计算配送费用
     * 
     * @param addressId 收货地址ID
     * @param productQuantity 商品数量
     * @param deliveryType 配送类型
     * @return 配送费用
     */
    BigDecimal calculateDeliveryFee(Long addressId, BigDecimal productQuantity, Integer deliveryType);

    /**
     * 统计用户配送订单数量
     * 
     * @param userId 用户ID
     * @param deliveryStatus 配送状态（可选）
     * @return 订单数量
     */
    int countUserDeliveries(Long userId, Integer deliveryStatus);

    /**
     * 获取配送统计信息
     * 
     * @param userId 用户ID（可选）
     * @return 统计信息
     */
    Object getDeliveryStatistics(Long userId);

    /**
     * 获取物流公司列表
     * 
     * @return 物流公司列表
     */
    List<String> getLogisticsCompanies();

    /**
     * 批量发货
     * 
     * @param orderIds 订单ID列表
     * @param logisticsCompany 物流公司
     * @return 发货结果
     */
    boolean batchShipOrders(List<Long> orderIds, String logisticsCompany);

    /**
     * 同步物流状态
     * 
     * @param trackingNumber 物流单号
     * @return 同步结果
     */
    boolean syncLogisticsStatus(String trackingNumber);

    /**
     * 获取待发货订单列表
     * 
     * @return 待发货订单列表
     */
    List<DeliveryOrder> getPendingShipmentOrders();

    /**
     * 获取配送中订单列表
     * 
     * @return 配送中订单列表
     */
    List<DeliveryOrder> getInTransitOrders();

    /**
     * 检查是否可以创建配送订单
     * 
     * @param adoptionRecordId 认养记录ID
     * @return 是否可以创建
     */
    boolean canCreateDeliveryOrder(Long adoptionRecordId);
}
