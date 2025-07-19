package com.ifarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifarm.entity.AdoptionOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 认养订单Mapper接口
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Mapper
public interface AdoptionOrderMapper extends BaseMapper<AdoptionOrder> {

    /**
     * 根据用户ID查询订单列表
     * 
     * @param userId 用户ID
     * @return 订单列表
     */
    List<AdoptionOrder> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据项目ID查询订单列表
     * 
     * @param projectId 项目ID
     * @return 订单列表
     */
    List<AdoptionOrder> selectByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据订单号查询订单
     * 
     * @param orderNo 订单号
     * @return 订单信息
     */
    AdoptionOrder selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据订单状态查询订单列表
     * 
     * @param orderStatus 订单状态
     * @return 订单列表
     */
    List<AdoptionOrder> selectByOrderStatus(@Param("orderStatus") Integer orderStatus);

    /**
     * 分页查询用户订单列表
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param orderStatus 订单状态（可选）
     * @return 分页结果
     */
    IPage<AdoptionOrder> selectUserOrderPage(Page<AdoptionOrder> page,
                                             @Param("userId") Long userId,
                                             @Param("orderStatus") Integer orderStatus);

    /**
     * 查询超时未支付的订单
     * 
     * @param timeoutTime 超时时间
     * @return 超时订单列表
     */
    List<AdoptionOrder> selectTimeoutOrders(@Param("timeoutTime") LocalDateTime timeoutTime);

    /**
     * 统计用户订单数量
     * 
     * @param userId 用户ID
     * @param orderStatus 订单状态（可选）
     * @return 订单数量
     */
    int countUserOrders(@Param("userId") Long userId, @Param("orderStatus") Integer orderStatus);

    /**
     * 查询订单详情（包含项目和作物信息）
     * 
     * @param orderId 订单ID
     * @return 订单详情
     */
    AdoptionOrder selectOrderDetail(@Param("orderId") Long orderId);
}
