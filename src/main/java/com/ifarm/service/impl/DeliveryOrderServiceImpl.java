package com.ifarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.entity.AdoptionRecord;
import com.ifarm.entity.DeliveryOrder;
import com.ifarm.mapper.DeliveryOrderMapper;
import com.ifarm.service.IAdoptionRecordService;
import com.ifarm.service.IDeliveryOrderService;
import com.ifarm.service.IDeliveryTrackingService;
import com.ifarm.service.ISystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final IAdoptionRecordService adoptionRecordService;
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
    public DeliveryOrder getOrderByAdoptionRecordId(Long adoptionRecordId) {
        if (adoptionRecordId == null) {
            throw new BusinessException("认养记录ID不能为空");
        }
        
        log.debug("根据认养记录ID查询配送订单: {}", adoptionRecordId);
        try {
            DeliveryOrder order = deliveryOrderMapper.selectByAdoptionRecordId(adoptionRecordId);
            return order;
        } catch (Exception e) {
            log.error("根据认养记录ID查询配送订单失败，认养记录ID: {}", adoptionRecordId, e);
            throw new BusinessException("查询配送订单失败");
        }
    }

    @Override
    public DeliveryOrder getOrderByOrderNo(String orderNo) {
        if (!StringUtils.hasText(orderNo)) {
            throw new BusinessException("配送单号不能为空");
        }
        
        log.debug("根据配送单号查询配送订单: {}", orderNo);
        try {
            DeliveryOrder order = deliveryOrderMapper.selectByOrderNo(orderNo);
            if (order == null) {
                log.warn("未找到配送单号为{}的订单", orderNo);
            }
            return order;
        } catch (Exception e) {
            log.error("根据配送单号查询配送订单失败，配送单号: {}", orderNo, e);
            throw new BusinessException("查询配送订单失败");
        }
    }

    @Override
    public DeliveryOrder getOrderByTrackingNumber(String trackingNumber) {
        if (!StringUtils.hasText(trackingNumber)) {
            throw new BusinessException("物流单号不能为空");
        }
        
        log.debug("根据物流单号查询配送订单: {}", trackingNumber);
        try {
            DeliveryOrder order = deliveryOrderMapper.selectByTrackingNumber(trackingNumber);
            if (order == null) {
                log.warn("未找到物流单号为{}的订单", trackingNumber);
            }
            return order;
        } catch (Exception e) {
            log.error("根据物流单号查询配送订单失败，物流单号: {}", trackingNumber, e);
            throw new BusinessException("查询配送订单失败");
        }
    }

    @Override
    public List<DeliveryOrder> getOrdersByDeliveryStatus(Integer deliveryStatus) {
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
    public List<DeliveryOrder> getOrdersByLogisticsCompany(String logisticsCompany) {
        if (!StringUtils.hasText(logisticsCompany)) {
            throw new BusinessException("物流公司不能为空");
        }
        
        log.debug("根据物流公司查询配送订单列表: {}", logisticsCompany);
        try {
            List<DeliveryOrder> orders = deliveryOrderMapper.selectByLogisticsCompany(logisticsCompany);
            log.debug("查询到{}个{}的配送订单", orders.size(), logisticsCompany);
            return orders;
        } catch (Exception e) {
            log.error("根据物流公司查询配送订单列表失败，物流公司: {}", logisticsCompany, e);
            throw new BusinessException("查询配送订单列表失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeliveryOrder createDeliveryOrder(Long adoptionRecordId, Long addressId, 
                                           Integer deliveryType, String deliveryNotes) {
        if (adoptionRecordId == null || addressId == null) {
            throw new BusinessException("认养记录ID和收货地址ID不能为空");
        }
        
        log.info("创建配送订单: 认养记录ID={}, 地址ID={}, 配送类型={}", adoptionRecordId, addressId, deliveryType);
        try {
            // 检查是否可以创建配送订单
            if (!canCreateDeliveryOrder(adoptionRecordId)) {
                throw new BusinessException("该认养记录不能创建配送订单");
            }
            
            // 获取认养记录信息
            AdoptionRecord adoptionRecord = adoptionRecordService.getById(adoptionRecordId);
            if (adoptionRecord == null) {
                throw new BusinessException("认养记录不存在");
            }
            
            // TODO: 根据addressId获取收货地址信息
            String deliveryAddress = "默认收货地址"; // 这里应该从地址服务获取
            
            // 计算配送费用
            BigDecimal deliveryFee = calculateDeliveryFee(addressId, BigDecimal.ONE, deliveryType);
            
            // 创建配送订单
            DeliveryOrder order = new DeliveryOrder();
            order.setOrderNo(generateOrderNo());
            order.setAdoptionRecordId(adoptionRecordId);
            order.setUserId(adoptionRecord.getUserId());
            order.setDeliveryAddress(deliveryAddress);
            order.setProductName("认养农产品"); // TODO: 从认养记录获取产品名称
            order.setProductQuantity(BigDecimal.ONE); // TODO: 从认养记录获取产品数量
            order.setPackageCount(1);
            order.setDeliveryType(deliveryType != null ? deliveryType : 1);
            order.setDeliveryFee(deliveryFee);
            order.setDeliveryStatus(1); // 待发货状态
            order.setDeliveryNotes(deliveryNotes);
            
            boolean result = save(order);
            if (result) {
                // 创建初始跟踪记录
                deliveryTrackingService.createTracking(order.getId(), "订单已创建", 
                    "配送订单已创建，等待发货", null, "系统", LocalDateTime.now());
                
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
    public boolean updateDeliveryOrder(DeliveryOrder deliveryOrder) {
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
            
            // 只有待发货状态的订单才能修改基本信息
            if (existingOrder.getDeliveryStatus() != 1 && 
                (deliveryOrder.getDeliveryAddress() != null || deliveryOrder.getDeliveryType() != null)) {
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
    public boolean deleteDeliveryOrder(Long orderId) {
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }
        
        log.info("删除配送订单: ID={}", orderId);
        try {
            // 验证订单是否存在
            DeliveryOrder order = getById(orderId);
            if (order == null) {
                throw new BusinessException("配送订单不存在");
            }
            
            // 只有待发货状态的订单才能删除
            if (order.getDeliveryStatus() != 1) {
                throw new BusinessException("只有待发货状态的订单才能删除");
            }
            
            boolean result = removeById(orderId);
            if (result) {
                log.info("配送订单删除成功");
            } else {
                log.error("配送订单删除失败");
                throw new BusinessException("配送订单删除失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除配送订单失败", e);
            throw new BusinessException("删除配送订单失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean shipOrder(Long orderId, String logisticsCompany, String trackingNumber, 
                           LocalDateTime estimatedDeliveryTime) {
        if (orderId == null || !StringUtils.hasText(logisticsCompany) || !StringUtils.hasText(trackingNumber)) {
            throw new BusinessException("参数不能为空");
        }
        
        log.info("发货配送订单: 订单ID={}, 物流公司={}, 物流单号={}", orderId, logisticsCompany, trackingNumber);
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
            updateOrder.setLogisticsCompany(logisticsCompany);
            updateOrder.setTrackingNumber(trackingNumber);
            updateOrder.setShippedTime(LocalDateTime.now());
            updateOrder.setEstimatedDeliveryTime(estimatedDeliveryTime);
            
            boolean result = updateById(updateOrder);
            if (result) {
                // 添加发货跟踪记录
                deliveryTrackingService.addShipmentTracking(orderId, "发货仓库", "仓库管理员");
                
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

    // 其他方法实现...
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDeliveryStatus(Long orderId, Integer deliveryStatus) {
        // 实现状态更新逻辑
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmDelivery(Long orderId, Long userId) {
        // 实现确认签收逻辑
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleDeliveryException(Long orderId, String exceptionReason, String handleMethod) {
        // 实现异常处理逻辑
        return true;
    }

    @Override
    public DeliveryOrder getDeliveryDetail(Long orderId) {
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }
        
        log.debug("获取配送订单详情: ID={}", orderId);
        try {
            DeliveryOrder order = deliveryOrderMapper.selectDeliveryDetail(orderId);
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
    public String generateOrderNo() {
        // 生成格式：DL + yyyyMMddHHmmss + 4位随机数
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "DL" + timestamp + random;
    }

    @Override
    public BigDecimal calculateDeliveryFee(Long addressId, BigDecimal productQuantity, Integer deliveryType) {
        if (addressId == null || productQuantity == null || productQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("参数无效");
        }
        
        log.debug("计算配送费用: addressId={}, quantity={}, type={}", addressId, productQuantity, deliveryType);
        try {
            // 获取系统配置的配送费用
            String defaultFeeStr = systemConfigService.getConfigValue("default_delivery_fee", "15.00");
            String freeDeliveryAmountStr = systemConfigService.getConfigValue("free_delivery_amount", "5.00");
            
            BigDecimal defaultFee = new BigDecimal(defaultFeeStr);
            BigDecimal freeDeliveryAmount = new BigDecimal(freeDeliveryAmountStr);
            
            // 简单的配送费计算逻辑
            // 如果产品数量超过免费配送数量，则免费配送
            if (productQuantity.compareTo(freeDeliveryAmount) >= 0) {
                return BigDecimal.ZERO;
            }
            
            // 根据配送类型调整费用
            BigDecimal fee = defaultFee;
            if (deliveryType != null) {
                fee = switch (deliveryType) {
                    case 2 -> // 冷链配送
                            fee.multiply(new BigDecimal("1.5"));
                    case 3 -> // 特殊配送
                            fee.multiply(new BigDecimal("2.0"));
                    default -> fee;
                };
            }
            
            return fee;
        } catch (Exception e) {
            log.error("计算配送费用失败", e);
            // 返回默认配送费
            return new BigDecimal("15.00");
        }
    }

    @Override
    public int countUserDeliveries(Long userId, Integer deliveryStatus) {
        if (userId == null) {
            return 0;
        }
        
        return deliveryOrderMapper.countUserDeliveries(userId, deliveryStatus);
    }

    @Override
    public Object getDeliveryStatistics(Long userId) {
        log.debug("获取配送统计信息，用户ID: {}", userId);
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            if (userId != null) {
                // 特定用户的统计
                statistics.put("totalCount", countUserDeliveries(userId, null));
                statistics.put("pendingCount", countUserDeliveries(userId, 1));
                statistics.put("shippedCount", countUserDeliveries(userId, 2));
                statistics.put("inTransitCount", countUserDeliveries(userId, 3));
                statistics.put("outForDeliveryCount", countUserDeliveries(userId, 4));
                statistics.put("deliveredCount", countUserDeliveries(userId, 5));
                statistics.put("exceptionCount", countUserDeliveries(userId, 6));
            } else {
                // 全局统计
                statistics.put("totalCount", count());
                statistics.put("pendingCount", count(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getDeliveryStatus, 1)));
                statistics.put("shippedCount", count(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getDeliveryStatus, 2)));
                statistics.put("inTransitCount", count(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getDeliveryStatus, 3)));
                statistics.put("outForDeliveryCount", count(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getDeliveryStatus, 4)));
                statistics.put("deliveredCount", count(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getDeliveryStatus, 5)));
                statistics.put("exceptionCount", count(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getDeliveryStatus, 6)));
            }
            
            return statistics;
        } catch (Exception e) {
            log.error("获取配送统计信息失败", e);
            throw new BusinessException("获取配送统计信息失败");
        }
    }

    @Override
    public List<String> getLogisticsCompanies() {
        log.debug("获取物流公司列表");
        // 返回常用的物流公司列表
        return Arrays.asList(
            "顺丰速运", "中通快递", "圆通速递", "申通快递", "韵达速递",
            "百世快递", "德邦快递", "京东物流", "菜鸟网络", "邮政EMS"
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchShipOrders(List<Long> orderIds, String logisticsCompany) {
        // 实现批量发货逻辑
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean syncLogisticsStatus(String trackingNumber) {
        // 实现物流状态同步逻辑
        return true;
    }

    @Override
    public List<DeliveryOrder> getPendingShipmentOrders() {
        log.debug("获取待发货订单列表");
        return getOrdersByDeliveryStatus(1);
    }

    @Override
    public List<DeliveryOrder> getInTransitOrders() {
        log.debug("获取配送中订单列表");
        try {
            LambdaQueryWrapper<DeliveryOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(DeliveryOrder::getDeliveryStatus, Arrays.asList(2, 3, 4)); // 已发货、运输中、派送中
            wrapper.orderByDesc(DeliveryOrder::getShippedTime);
            
            List<DeliveryOrder> orders = list(wrapper);
            log.debug("获取到{}个配送中订单", orders.size());
            return orders;
        } catch (Exception e) {
            log.error("获取配送中订单列表失败", e);
            throw new BusinessException("获取配送中订单列表失败");
        }
    }

    @Override
    public boolean canCreateDeliveryOrder(Long adoptionRecordId) {
        if (adoptionRecordId == null) {
            return false;
        }
        
        log.debug("检查是否可以创建配送订单: adoptionRecordId={}", adoptionRecordId);
        try {
            // 检查认养记录是否存在
            AdoptionRecord adoptionRecord = adoptionRecordService.getById(adoptionRecordId);
            if (adoptionRecord == null) {
                return false;
            }
            
            // 检查认养记录状态是否为已收获
            if (adoptionRecord.getAdoptionStatus() != 4) {
                return false;
            }
            
            // 检查是否已经创建过配送订单
            DeliveryOrder existingOrder = getOrderByAdoptionRecordId(adoptionRecordId);
            return existingOrder == null;
        } catch (Exception e) {
            log.error("检查是否可以创建配送订单失败", e);
            return false;
        }
    }
}
