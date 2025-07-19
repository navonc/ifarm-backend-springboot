package com.ifarm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ifarm.entity.AdoptionOrder;

import java.math.BigDecimal;
import java.util.List;

/**
 * 认养订单服务接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public interface IAdoptionOrderService extends IService<AdoptionOrder> {

    /**
     * 根据用户ID查询订单列表
     * 
     * @param userId 用户ID
     * @return 订单列表
     */
    List<AdoptionOrder> getOrdersByUserId(Long userId);

    /**
     * 根据项目ID查询订单列表
     * 
     * @param projectId 项目ID
     * @return 订单列表
     */
    List<AdoptionOrder> getOrdersByProjectId(Long projectId);

    /**
     * 根据订单号查询订单
     * 
     * @param orderNo 订单号
     * @return 订单信息
     */
    AdoptionOrder getOrderByOrderNo(String orderNo);

    /**
     * 根据订单状态查询订单列表
     * 
     * @param orderStatus 订单状态
     * @return 订单列表
     */
    List<AdoptionOrder> getOrdersByStatus(Integer orderStatus);

    /**
     * 分页查询用户订单列表
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param orderStatus 订单状态（可选）
     * @return 分页结果
     */
    IPage<AdoptionOrder> getUserOrderPage(Page<AdoptionOrder> page, Long userId, Integer orderStatus);

    /**
     * 创建认养订单
     * 
     * @param userId 用户ID
     * @param projectId 项目ID
     * @param unitCount 认养单元数量
     * @param remark 订单备注
     * @return 订单信息
     */
    AdoptionOrder createOrder(Long userId, Long projectId, Integer unitCount, String remark);

    /**
     * 取消订单
     * 
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 取消结果
     */
    boolean cancelOrder(Long orderId, Long userId);

    /**
     * 支付订单
     * 
     * @param orderId 订单ID
     * @param paymentMethod 支付方式
     * @param paymentNo 支付流水号
     * @return 支付结果
     */
    boolean payOrder(Long orderId, String paymentMethod, String paymentNo);

    /**
     * 完成订单
     * 
     * @param orderId 订单ID
     * @return 完成结果
     */
    boolean completeOrder(Long orderId);

    /**
     * 申请退款
     * 
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param reason 退款原因
     * @return 申请结果
     */
    boolean applyRefund(Long orderId, Long userId, String reason);

    /**
     * 处理退款
     * 
     * @param orderId 订单ID
     * @param approved 是否同意退款
     * @param remark 处理备注
     * @return 处理结果
     */
    boolean processRefund(Long orderId, Boolean approved, String remark);

    /**
     * 获取订单详情（包含项目和作物信息）
     * 
     * @param orderId 订单ID
     * @return 订单详情
     */
    AdoptionOrder getOrderDetail(Long orderId);

    /**
     * 检查用户是否有权限操作订单
     * 
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 是否有权限
     */
    boolean hasPermission(Long userId, Long orderId);

    /**
     * 生成订单号
     * 
     * @return 订单号
     */
    String generateOrderNo();

    /**
     * 查询超时未支付的订单
     * 
     * @param timeoutMinutes 超时分钟数
     * @return 超时订单列表
     */
    List<AdoptionOrder> getTimeoutOrders(Integer timeoutMinutes);

    /**
     * 自动取消超时订单
     * 
     * @param timeoutMinutes 超时分钟数
     * @return 取消的订单数量
     */
    int autoCancelTimeoutOrders(Integer timeoutMinutes);

    /**
     * 统计用户订单数量
     * 
     * @param userId 用户ID
     * @param orderStatus 订单状态（可选）
     * @return 订单数量
     */
    int countUserOrders(Long userId, Integer orderStatus);

    /**
     * 统计用户订单金额
     * 
     * @param userId 用户ID
     * @param orderStatus 订单状态（可选）
     * @return 订单总金额
     */
    BigDecimal sumUserOrderAmount(Long userId, Integer orderStatus);

    /**
     * 获取订单统计信息
     * 
     * @param userId 用户ID（可选）
     * @return 统计信息
     */
    Object getOrderStatistics(Long userId);

    /**
     * 计算订单金额
     * 
     * @param projectId 项目ID
     * @param unitCount 单元数量
     * @return 订单金额信息
     */
    Object calculateOrderAmount(Long projectId, Integer unitCount);

    /**
     * 验证订单支付回调
     * 
     * @param orderNo 订单号
     * @param paymentNo 支付流水号
     * @param amount 支付金额
     * @return 验证结果
     */
    boolean verifyPaymentCallback(String orderNo, String paymentNo, BigDecimal amount);
}
