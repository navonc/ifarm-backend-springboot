package com.ifarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.entity.DeliveryOrder;
import com.ifarm.mapper.DeliveryOrderMapper;
import com.ifarm.service.IDeliveryOrderService;
import com.ifarm.service.IDeliveryTrackingService;
import com.ifarm.service.IHarvestRecordService;
import com.ifarm.service.ISystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 配送订单服务实现类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryOrderServiceImpl extends ServiceImpl<DeliveryOrderMapper, DeliveryOrder> implements IDeliveryOrderService {

    private final DeliveryOrderMapper deliveryOrderMapper;
    private final IDeliveryTrackingService deliveryTrackingService;
    private final IHarvestRecordService harvestRecordService;
    private final ISystemConfigService systemConfigService;

    @Override
    public List<DeliveryOrder> getOrdersByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        log.debug("根据用户ID查询配送订单列表: {}", userId);
        try {
            List<DeliveryOrder> orders = deliveryOrderMapper.selectByUserId(userId);
            log.debug("查询到{}个配送订单", orders.size());
            return orders;
        } catch (Exception e) {
            log.error("根据用户ID查询配送订单列表失败，用户ID: {}", userId, e);
            throw new BusinessException("查询配送订单列表失败");
        }
    }

    @Override
    public List<DeliveryOrder> getOrdersByHarvestRecordId(Long harvestRecordId) {
        if (harvestRecordId == null) {
            throw new BusinessException("收获记录ID不能为空");
        }
        
        log.debug("根据收获记录ID查询配送订单列表: {}", harvestRecordId);
        try {
            List<DeliveryOrder> orders = deliveryOrderMapper.selectByHarvestRecordId(harvestRecordId);
            log.debug("查询到{}个配送订单", orders.size());
            return orders;
        } catch (Exception e) {
            log.error("根据收获记录ID查询配送订单列表失败，收获记录ID: {}", harvestRecordId, e);
            throw new BusinessException("查询配送订单列表失败");
        }
    }

    @Override
    public DeliveryOrder getOrderByOrderNo(String orderNo) {
        if (!StringUtils.hasText(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }
        
        log.debug("根据订单号查询配送订单: {}", orderNo);
        try {
            DeliveryOrder order = deliveryOrderMapper.selectByOrderNo(orderNo);
            if (order == null) {
                log.warn("未找到订单号为{}的配送订单", orderNo);
            }
            return order;
        } catch (Exception e) {
            log.error("根据订单号查询配送订单失败，订单号: {}", orderNo, e);
            throw new BusinessException("查询配送订单失败");
        }
    }

    @Override
    public List<DeliveryOrder> getOrdersByStatus(Integer deliveryStatus) {
        if (deliveryStatus == null) {
            throw new BusinessException("配送状态不能为空");
        }
        
        log.debug("根据配送状态查询订单列表: {}", deliveryStatus);
        try {
            List<DeliveryOrder> orders = deliveryOrderMapper.selectByDeliveryStatus(deliveryStatus);
            log.debug("查询到{}个状态为{}的配送订单", orders.size(), deliveryStatus);
            return orders;
        } catch (Exception e) {
            log.error("根据配送状态查询订单列表失败，状态: {}", deliveryStatus, e);
            throw new BusinessException("查询配送订单列表失败");
        }
    }

    @Override
    public IPage<DeliveryOrder> getUserDeliveryPage(Page<DeliveryOrder> page, Long userId, Integer deliveryStatus) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        log.debug("分页查询用户配送订单: userId={}, deliveryStatus={}", userId, deliveryStatus);
        try {
            IPage<DeliveryOrder> result = deliveryOrderMapper.selectUserDeliveryPage(page, userId, deliveryStatus);
            log.debug("查询到{}条配送订单记录", result.getRecords().size());
            return result;
        } catch (Exception e) {
            log.error("分页查询用户配送订单失败", e);
            throw new BusinessException("查询配送订单失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeliveryOrder createOrder(Long userId, Long harvestRecordId, String deliveryAddress, 
                                   String contactPhone, String remark) {
        if (userId == null || harvestRecordId == null) {
            throw new BusinessException("参数不能为空");
        }
        
        if (!StringUtils.hasText(deliveryAddress)) {
            throw new BusinessException("配送地址不能为空");
        }
        
        if (!StringUtils.hasText(contactPhone)) {
            throw new BusinessException("联系电话不能为空");
        }
        
        log.info("创建配送订单: 用户ID={}, 收获记录ID={}", userId, harvestRecordId);
        try {
            // 验证收获记录是否存在且属于该用户
            HarvestRecord harvestRecord = harvestRecordService.getById(harvestRecordId);
            if (harvestRecord == null) {
                throw new BusinessException("收获记录不存在");
            }
            
            if (!harvestRecord.getUserId().equals(userId)) {
                throw new BusinessException("无权限操作该收获记录");
            }
            
            // 检查是否已经创建过配送订单
            if (existsByHarvestRecordId(harvestRecordId)) {
                throw new BusinessException("该收获记录已创建配送订单");
            }
            
            // 计算配送费用
            BigDecimal deliveryFee = calculateDeliveryFee(harvestRecord.getActualYield(), deliveryAddress);
            
            // 创建配送订单
            DeliveryOrder order = new DeliveryOrder();
            order.setOrderNo(generateOrderNo());
            order.setUserId(userId);
            order.setHarvestRecordId(harvestRecordId);
            order.setDeliveryAddress(deliveryAddress);
            order.setContactPhone(contactPhone);
            order.setDeliveryFee(deliveryFee);
            order.setDeliveryStatus(1); // 待发货状态
            order.setRemark(remark);
            
            boolean result = save(order);
            if (result) {
                // 创建初始物流跟踪记录
                deliveryTrackingService.createInitialTracking(order.getId());
                
                log.info("配送订单创建成功，订单号: {}", order.getOrderNo());
                return order;
            } else {
                log.error("配送订单创建失败");
                throw new BusinessException("配送订单创建失败");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建配送订单失败", e);
            throw new BusinessException("创建配送订单失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrder(DeliveryOrder deliveryOrder) {
        if (deliveryOrder == null || deliveryOrder.getId() == null) {
            throw new BusinessException("配送订单信息不完整");
        }
        
        log.info("更新配送订单: ID={}", deliveryOrder.getId());
        try {
            // 验证订单是否存在
            DeliveryOrder existingOrder = getById(deliveryOrder.getId());
            if (existingOrder == null) {
                throw new BusinessException("配送订单不存在");
            }
            
            // 只有待发货状态的订单才能修改地址等信息
            if (existingOrder.getDeliveryStatus() != 1 && 
                (deliveryOrder.getDeliveryAddress() != null || deliveryOrder.getContactPhone() != null)) {
                throw new BusinessException("订单已发货，无法修改配送信息");
            }
            
            boolean result = updateById(deliveryOrder);
            if (result) {
                log.info("配送订单更新成功");
            } else {
                log.error("配送订单更新失败");
                throw new BusinessException("配送订单更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新配送订单失败", e);
            throw new BusinessException("更新配送订单失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId, Long userId) {
        if (orderId == null || userId == null) {
            throw new BusinessException("参数不能为空");
        }
        
        log.info("取消配送订单: 订单ID={}, 用户ID={}", orderId, userId);
        try {
            // 验证订单是否存在且属于该用户
            DeliveryOrder order = getById(orderId);
            if (order == null) {
                throw new BusinessException("配送订单不存在");
            }
            
            if (!order.getUserId().equals(userId)) {
                throw new BusinessException("无权限操作该订单");
            }
            
            // 只有待发货状态的订单才能取消
            if (order.getDeliveryStatus() != 1) {
                throw new BusinessException("订单状态不允许取消");
            }
            
            // 更新订单状态为已取消
            DeliveryOrder updateOrder = new DeliveryOrder();
            updateOrder.setId(orderId);
            updateOrder.setDeliveryStatus(5); // 已取消状态
            
            boolean result = updateById(updateOrder);
            if (result) {
                // 添加物流跟踪记录
                deliveryTrackingService.addTrackingRecord(orderId, "订单已取消", "用户取消订单");
                
                log.info("配送订单取消成功");
            } else {
                log.error("配送订单取消失败");
                throw new BusinessException("配送订单取消失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("取消配送订单失败", e);
            throw new BusinessException("取消配送订单失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean shipOrder(Long orderId, String courierCompany, String trackingNumber) {
        if (orderId == null || !StringUtils.hasText(courierCompany) || !StringUtils.hasText(trackingNumber)) {
            throw new BusinessException("参数不能为空");
        }
        
        log.info("发货配送订单: 订单ID={}, 快递公司={}, 快递单号={}", orderId, courierCompany, trackingNumber);
        try {
            // 验证订单是否存在
            DeliveryOrder order = getById(orderId);
            if (order == null) {
                throw new BusinessException("配送订单不存在");
            }
            
            // 只有待发货状态的订单才能发货
            if (order.getDeliveryStatus() != 1) {
                throw new BusinessException("订单状态不允许发货");
            }
            
            // 更新订单发货信息
            DeliveryOrder updateOrder = new DeliveryOrder();
            updateOrder.setId(orderId);
            updateOrder.setDeliveryStatus(2); // 已发货状态
            updateOrder.setCourierCompany(courierCompany);
            updateOrder.setTrackingNumber(trackingNumber);
            updateOrder.setShipTime(LocalDateTime.now());
            
            boolean result = updateById(updateOrder);
            if (result) {
                // 添加物流跟踪记录
                deliveryTrackingService.addTrackingRecord(orderId, "商品已发货", 
                    String.format("快递公司：%s，快递单号：%s", courierCompany, trackingNumber));
                
                log.info("配送订单发货成功");
            } else {
                log.error("配送订单发货失败");
                throw new BusinessException("配送订单发货失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("发货配送订单失败", e);
            throw new BusinessException("发货配送订单失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDeliveryStatus(Long orderId, Integer deliveryStatus, String statusRemark) {
        if (orderId == null || deliveryStatus == null) {
            throw new BusinessException("参数不能为空");
        }
        
        if (deliveryStatus < 1 || deliveryStatus > 5) {
            throw new BusinessException("配送状态值无效");
        }
        
        log.info("更新配送状态: 订单ID={}, 状态={}, 备注={}", orderId, deliveryStatus, statusRemark);
        try {
            // 验证状态流转规则
            DeliveryOrder existingOrder = getById(orderId);
            if (existingOrder == null) {
                throw new BusinessException("配送订单不存在");
            }
            
            validateStatusTransition(existingOrder.getDeliveryStatus(), deliveryStatus);
            
            DeliveryOrder order = new DeliveryOrder();
            order.setId(orderId);
            order.setDeliveryStatus(deliveryStatus);
            
            // 根据状态设置相应的时间字段
            LocalDateTime now = LocalDateTime.now();
            switch (deliveryStatus) {
                case 2: // 已发货
                    order.setShipTime(now);
                    break;
                case 3: // 运输中
                    // 不设置时间，由物流跟踪更新
                    break;
                case 4: // 已送达
                    order.setDeliveryTime(now);
                    break;
            }
            
            boolean result = updateById(order);
            if (result) {
                // 添加物流跟踪记录
                String statusDesc = getStatusDescription(deliveryStatus);
                deliveryTrackingService.addTrackingRecord(orderId, statusDesc, statusRemark);
                
                log.info("配送状态更新成功");
            } else {
                log.error("配送状态更新失败");
                throw new BusinessException("配送状态更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新配送状态失败", e);
            throw new BusinessException("更新配送状态失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmDelivery(Long orderId, Long userId) {
        if (orderId == null || userId == null) {
            throw new BusinessException("参数不能为空");
        }
        
        log.info("确认收货: 订单ID={}, 用户ID={}", orderId, userId);
        try {
            // 验证订单是否存在且属于该用户
            DeliveryOrder order = getById(orderId);
            if (order == null) {
                throw new BusinessException("配送订单不存在");
            }
            
            if (!order.getUserId().equals(userId)) {
                throw new BusinessException("无权限操作该订单");
            }
            
            // 只有已送达状态的订单才能确认收货
            if (order.getDeliveryStatus() != 4) {
                throw new BusinessException("订单状态不允许确认收货");
            }
            
            // 更新订单状态为已完成
            boolean result = updateDeliveryStatus(orderId, 4, "用户确认收货");
            
            if (result) {
                log.info("确认收货成功");
            }
            return result;
        } catch (Exception e) {
            log.error("确认收货失败", e);
            throw new BusinessException("确认收货失败");
        }
    }

    @Override
    public DeliveryOrder getOrderDetail(Long orderId) {
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }
        
        log.debug("获取配送订单详情: ID={}", orderId);
        try {
            DeliveryOrder order = deliveryOrderMapper.selectOrderDetail(orderId);
            if (order == null) {
                throw new BusinessException("配送订单不存在");
            }
            return order;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取配送订单详情失败", e);
            throw new BusinessException("获取配送订单详情失败");
        }
    }

    @Override
    public boolean hasPermission(Long userId, Long orderId) {
        if (userId == null || orderId == null) {
            return false;
        }
        
        log.debug("检查用户配送订单操作权限: userId={}, orderId={}", userId, orderId);
        try {
            DeliveryOrder order = getById(orderId);
            if (order == null) {
                return false;
            }
            
            // 检查订单是否属于该用户
            return order.getUserId().equals(userId);
        } catch (Exception e) {
            log.error("检查用户配送订单操作权限失败", e);
            return false;
        }
    }

    @Override
    public boolean existsByHarvestRecordId(Long harvestRecordId) {
        if (harvestRecordId == null) {
            return false;
        }
        
        LambdaQueryWrapper<DeliveryOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryOrder::getHarvestRecordId, harvestRecordId);
        return count(wrapper) > 0;
    }

    @Override
    public String generateOrderNo() {
        // 生成格式：DL + yyyyMMddHHmmss + 4位随机数
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "DL" + timestamp + random;
    }

    @Override
    public BigDecimal calculateDeliveryFee(BigDecimal weight, String deliveryAddress) {
        if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("重量参数无效");
        }
        
        log.debug("计算配送费用: weight={}, address={}", weight, deliveryAddress);
        try {
            // 获取系统配置的配送费用
            String defaultFeeStr = systemConfigService.getConfigValue("default_delivery_fee", "15.00");
            String freeFeeAmountStr = systemConfigService.getConfigValue("free_delivery_amount", "100.00");
            
            BigDecimal defaultFee = new BigDecimal(defaultFeeStr);
            BigDecimal freeDeliveryAmount = new BigDecimal(freeFeeAmountStr);
            
            // 简单的配送费计算逻辑
            // TODO: 可以根据重量、距离等因素进行更复杂的计算
            
            // 如果重量超过免费配送重量，则免费配送
            if (weight.compareTo(freeDeliveryAmount) >= 0) {
                return BigDecimal.ZERO;
            }
            
            return defaultFee;
        } catch (Exception e) {
            log.error("计算配送费用失败", e);
            // 返回默认配送费
            return new BigDecimal("15.00");
        }
    }

    @Override
    public int countUserOrders(Long userId, Integer deliveryStatus) {
        if (userId == null) {
            return 0;
        }
        
        return deliveryOrderMapper.countUserOrders(userId, deliveryStatus);
    }

    @Override
    public Object getDeliveryStatistics(Long userId) {
        log.debug("获取配送统计信息，用户ID: {}", userId);
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            if (userId != null) {
                // 特定用户的统计
                statistics.put("totalCount", countUserOrders(userId, null));
                statistics.put("pendingCount", countUserOrders(userId, 1));
                statistics.put("shippedCount", countUserOrders(userId, 2));
                statistics.put("inTransitCount", countUserOrders(userId, 3));
                statistics.put("deliveredCount", countUserOrders(userId, 4));
                statistics.put("cancelledCount", countUserOrders(userId, 5));
            } else {
                // 全局统计
                statistics.put("totalCount", count());
                statistics.put("pendingCount", count(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getDeliveryStatus, 1)));
                statistics.put("shippedCount", count(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getDeliveryStatus, 2)));
                statistics.put("inTransitCount", count(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getDeliveryStatus, 3)));
                statistics.put("deliveredCount", count(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getDeliveryStatus, 4)));
                statistics.put("cancelledCount", count(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getDeliveryStatus, 5)));
            }
            
            return statistics;
        } catch (Exception e) {
            log.error("获取配送统计信息失败", e);
            throw new BusinessException("获取配送统计信息失败");
        }
    }

    /**
     * 验证配送状态流转规则
     */
    private void validateStatusTransition(Integer currentStatus, Integer newStatus) {
        if (currentStatus == null || newStatus == null) {
            throw new BusinessException("状态不能为空");
        }
        
        if (currentStatus.equals(newStatus)) {
            return; // 状态未变更
        }
        
        // 定义允许的状态流转规则
        // 1-待发货 -> 2-已发货
        // 2-已发货 -> 3-运输中
        // 3-运输中 -> 4-已送达
        // 任何状态 -> 5-已取消（仅限待发货状态）
        
        boolean validTransition = false;
        
        switch (currentStatus) {
            case 1: // 待发货
                validTransition = newStatus == 2 || newStatus == 5;
                break;
            case 2: // 已发货
                validTransition = newStatus == 3;
                break;
            case 3: // 运输中
                validTransition = newStatus == 4;
                break;
            case 4: // 已送达
                validTransition = false; // 已送达状态不能变更
                break;
            case 5: // 已取消
                validTransition = false; // 已取消状态不能变更
                break;
        }
        
        if (!validTransition) {
            throw new BusinessException("无效的状态流转");
        }
    }

    /**
     * 获取状态描述
     */
    private String getStatusDescription(Integer status) {
        switch (status) {
            case 1: return "待发货";
            case 2: return "已发货";
            case 3: return "运输中";
            case 4: return "已送达";
            case 5: return "已取消";
            default: return "未知状态";
        }
    }
}
