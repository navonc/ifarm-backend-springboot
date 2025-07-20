package com.ifarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.entity.AdoptionOrder;
import com.ifarm.entity.AdoptionProject;
import com.ifarm.entity.AdoptionRecord;
import com.ifarm.mapper.AdoptionOrderMapper;
import com.ifarm.service.IAdoptionOrderService;
import com.ifarm.service.IAdoptionProjectService;
import com.ifarm.service.IAdoptionRecordService;
import com.ifarm.service.IProjectUnitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 认养订单服务实现类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Service
public class AdoptionOrderServiceImpl extends ServiceImpl<AdoptionOrderMapper, AdoptionOrder> implements IAdoptionOrderService {

    private final AdoptionOrderMapper adoptionOrderMapper;
    private final IAdoptionProjectService adoptionProjectService;
    private final IProjectUnitService projectUnitService;
    private final IAdoptionRecordService adoptionRecordService;

    public AdoptionOrderServiceImpl(AdoptionOrderMapper adoptionOrderMapper,
                                   IAdoptionProjectService adoptionProjectService,
                                   IProjectUnitService projectUnitService,
                                   @Lazy IAdoptionRecordService adoptionRecordService) {
        this.adoptionOrderMapper = adoptionOrderMapper;
        this.adoptionProjectService = adoptionProjectService;
        this.projectUnitService = projectUnitService;
        this.adoptionRecordService = adoptionRecordService;
    }

    @Override
    public List<AdoptionOrder> getOrdersByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        log.debug("根据用户ID查询订单列表: {}", userId);
        try {
            List<AdoptionOrder> orders = adoptionOrderMapper.selectByUserId(userId);
            log.debug("查询到{}个订单", orders.size());
            return orders;
        } catch (Exception e) {
            log.error("根据用户ID查询订单列表失败，用户ID: {}", userId, e);
            throw new BusinessException("查询订单列表失败");
        }
    }

    @Override
    public List<AdoptionOrder> getOrdersByProjectId(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        
        log.debug("根据项目ID查询订单列表: {}", projectId);
        try {
            List<AdoptionOrder> orders = adoptionOrderMapper.selectByProjectId(projectId);
            log.debug("查询到{}个订单", orders.size());
            return orders;
        } catch (Exception e) {
            log.error("根据项目ID查询订单列表失败，项目ID: {}", projectId, e);
            throw new BusinessException("查询订单列表失败");
        }
    }

    @Override
    public AdoptionOrder getOrderByOrderNo(String orderNo) {
        if (!StringUtils.hasText(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }
        
        log.debug("根据订单号查询订单: {}", orderNo);
        try {
            AdoptionOrder order = adoptionOrderMapper.selectByOrderNo(orderNo);
            if (order == null) {
                log.warn("未找到订单号为{}的订单", orderNo);
            }
            return order;
        } catch (Exception e) {
            log.error("根据订单号查询订单失败，订单号: {}", orderNo, e);
            throw new BusinessException("查询订单失败");
        }
    }

    @Override
    public List<AdoptionOrder> getOrdersByStatus(Integer orderStatus) {
        if (orderStatus == null) {
            throw new BusinessException("订单状态不能为空");
        }
        
        log.debug("根据订单状态查询订单列表: {}", orderStatus);
        try {
            List<AdoptionOrder> orders = adoptionOrderMapper.selectByOrderStatus(orderStatus);
            log.debug("查询到{}个状态为{}的订单", orders.size(), orderStatus);
            return orders;
        } catch (Exception e) {
            log.error("根据订单状态查询订单列表失败，状态: {}", orderStatus, e);
            throw new BusinessException("查询订单列表失败");
        }
    }

    @Override
    public IPage<AdoptionOrder> getUserOrderPage(Page<AdoptionOrder> page, Long userId, Integer orderStatus) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        log.debug("分页查询用户订单列表: userId={}, orderStatus={}", userId, orderStatus);
        try {
            IPage<AdoptionOrder> result = adoptionOrderMapper.selectUserOrderPage(page, userId, orderStatus);
            log.debug("查询到{}条订单记录", result.getRecords().size());
            return result;
        } catch (Exception e) {
            log.error("分页查询用户订单列表失败", e);
            throw new BusinessException("查询订单列表失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdoptionOrder createOrder(Long userId, Long projectId, Integer unitCount, String remark) {
        if (userId == null || projectId == null || unitCount == null || unitCount <= 0) {
            throw new BusinessException("参数无效");
        }
        
        log.info("创建认养订单: 用户ID={}, 项目ID={}, 单元数量={}", userId, projectId, unitCount);
        try {
            // 验证项目是否可以认养
            if (!adoptionProjectService.canAdopt(projectId, unitCount)) {
                throw new BusinessException("项目不可认养或单元数量不足");
            }
            
            // 获取项目信息
            AdoptionProject project = adoptionProjectService.getById(projectId);
            if (project == null) {
                throw new BusinessException("项目不存在");
            }
            
            // 计算订单金额
            Object amountInfo = calculateOrderAmount(projectId, unitCount);
            Map<String, Object> amountMap = (Map<String, Object>) amountInfo;
            BigDecimal totalAmount = (BigDecimal) amountMap.get("totalAmount");
            BigDecimal discountAmount = (BigDecimal) amountMap.get("discountAmount");
            BigDecimal actualAmount = (BigDecimal) amountMap.get("actualAmount");
            
            // 创建订单
            AdoptionOrder order = new AdoptionOrder();
            order.setOrderNo(generateOrderNo());
            order.setUserId(userId);
            order.setProjectId(projectId);
            order.setUnitCount(unitCount);
            order.setUnitPrice(project.getUnitPrice());
            order.setTotalAmount(totalAmount);
            order.setDiscountAmount(discountAmount);
            order.setActualAmount(actualAmount);
            order.setOrderStatus(1); // 待支付状态
            order.setRemark(remark);
            
            boolean result = save(order);
            if (result) {
                // 预占用项目单元
                adoptionProjectService.updateAvailableUnits(projectId, -unitCount);
                
                log.info("认养订单创建成功，订单号: {}", order.getOrderNo());
                return order;
            } else {
                log.error("认养订单创建失败");
                throw new BusinessException("认养订单创建失败");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建认养订单失败", e);
            throw new BusinessException("创建认养订单失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId, Long userId) {
        if (orderId == null || userId == null) {
            throw new BusinessException("参数不能为空");
        }
        
        log.info("取消订单: 订单ID={}, 用户ID={}", orderId, userId);
        try {
            // 验证订单是否存在且属于该用户
            AdoptionOrder order = getById(orderId);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            
            if (!order.getUserId().equals(userId)) {
                throw new BusinessException("无权限操作该订单");
            }
            
            // 只有待支付状态的订单才能取消
            if (order.getOrderStatus() != 1) {
                throw new BusinessException("订单状态不允许取消");
            }
            
            // 更新订单状态为已取消
            AdoptionOrder updateOrder = new AdoptionOrder();
            updateOrder.setId(orderId);
            updateOrder.setOrderStatus(4); // 已取消状态
            
            boolean result = updateById(updateOrder);
            if (result) {
                // 释放预占用的项目单元
                adoptionProjectService.updateAvailableUnits(order.getProjectId(), order.getUnitCount());
                
                log.info("订单取消成功");
            } else {
                log.error("订单取消失败");
                throw new BusinessException("订单取消失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("取消订单失败", e);
            throw new BusinessException("取消订单失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(Long orderId, String paymentMethod, String paymentNo) {
        if (orderId == null || !StringUtils.hasText(paymentMethod) || !StringUtils.hasText(paymentNo)) {
            throw new BusinessException("参数不能为空");
        }
        
        log.info("支付订单: 订单ID={}, 支付方式={}, 支付流水号={}", orderId, paymentMethod, paymentNo);
        try {
            // 验证订单是否存在
            AdoptionOrder order = getById(orderId);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            
            // 只有待支付状态的订单才能支付
            if (order.getOrderStatus() != 1) {
                throw new BusinessException("订单状态不允许支付");
            }
            
            // 更新订单支付信息
            AdoptionOrder updateOrder = new AdoptionOrder();
            updateOrder.setId(orderId);
            updateOrder.setOrderStatus(2); // 已支付状态
            updateOrder.setPaymentMethod(paymentMethod);
            updateOrder.setPaymentNo(paymentNo);
            updateOrder.setPaymentTime(LocalDateTime.now());
            
            boolean result = updateById(updateOrder);
            if (result) {
                // 分配项目单元
                List<Long> allocatedUnitIds = projectUnitService.allocateUnits(order.getProjectId(), order.getUnitCount());
                
                // 创建认养记录
                adoptionRecordService.createRecords(orderId, allocatedUnitIds);
                
                log.info("订单支付成功，分配{}个单元", allocatedUnitIds.size());
            } else {
                log.error("订单支付失败");
                throw new BusinessException("订单支付失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("支付订单失败", e);
            throw new BusinessException("支付订单失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeOrder(Long orderId) {
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }
        
        log.info("完成订单: 订单ID={}", orderId);
        try {
            // 验证订单是否存在
            AdoptionOrder order = getById(orderId);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            
            // 只有已支付状态的订单才能完成
            if (order.getOrderStatus() != 2) {
                throw new BusinessException("订单状态不允许完成");
            }
            
            // 更新订单状态为已完成
            AdoptionOrder updateOrder = new AdoptionOrder();
            updateOrder.setId(orderId);
            updateOrder.setOrderStatus(3); // 已完成状态
            
            boolean result = updateById(updateOrder);
            if (result) {
                log.info("订单完成成功");
            } else {
                log.error("订单完成失败");
                throw new BusinessException("订单完成失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("完成订单失败", e);
            throw new BusinessException("完成订单失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applyRefund(Long orderId, Long userId, String reason) {
        if (orderId == null || userId == null) {
            throw new BusinessException("参数不能为空");
        }
        
        log.info("申请退款: 订单ID={}, 用户ID={}, 原因={}", orderId, userId, reason);
        try {
            // 验证订单是否存在且属于该用户
            AdoptionOrder order = getById(orderId);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            
            if (!order.getUserId().equals(userId)) {
                throw new BusinessException("无权限操作该订单");
            }
            
            // 只有已支付状态的订单才能申请退款
            if (order.getOrderStatus() != 2) {
                throw new BusinessException("订单状态不允许申请退款");
            }
            
            // TODO: 检查退款条件，如项目是否已开始种植等
            
            // 更新订单状态为申请退款（这里简化处理，直接退款）
            AdoptionOrder updateOrder = new AdoptionOrder();
            updateOrder.setId(orderId);
            updateOrder.setOrderStatus(5); // 已退款状态
            
            boolean result = updateById(updateOrder);
            if (result) {
                // 释放已分配的单元
                List<AdoptionRecord> records = adoptionRecordService.getRecordsByOrderId(orderId);
                List<Long> unitIds = records.stream().map(AdoptionRecord::getUnitId).toList();
                
                if (!unitIds.isEmpty()) {
                    projectUnitService.releaseUnits(unitIds);
                }
                
                // 恢复项目可用单元数
                adoptionProjectService.updateAvailableUnits(order.getProjectId(), order.getUnitCount());
                
                log.info("退款申请成功");
            } else {
                log.error("退款申请失败");
                throw new BusinessException("退款申请失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("申请退款失败", e);
            throw new BusinessException("申请退款失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processRefund(Long orderId, Boolean approved, String remark) {
        if (orderId == null || approved == null) {
            throw new BusinessException("参数不能为空");
        }
        
        log.info("处理退款: 订单ID={}, 是否同意={}, 备注={}", orderId, approved, remark);
        try {
            // 验证订单是否存在
            AdoptionOrder order = getById(orderId);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            
            // TODO: 验证订单状态是否为申请退款中
            
            Integer newStatus = approved ? 5 : 2; // 5-已退款，2-已支付（拒绝退款）
            
            // 更新订单状态
            AdoptionOrder updateOrder = new AdoptionOrder();
            updateOrder.setId(orderId);
            updateOrder.setOrderStatus(newStatus);
            
            boolean result = updateById(updateOrder);
            if (result) {
                if (approved) {
                    // 同意退款的后续处理
                    log.info("退款处理成功，已同意退款");
                } else {
                    // 拒绝退款的后续处理
                    log.info("退款处理成功，已拒绝退款");
                }
            } else {
                log.error("退款处理失败");
                throw new BusinessException("退款处理失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("处理退款失败", e);
            throw new BusinessException("处理退款失败");
        }
    }

    @Override
    public AdoptionOrder getOrderDetail(Long orderId) {
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }
        
        log.debug("获取订单详情: ID={}", orderId);
        try {
            AdoptionOrder order = adoptionOrderMapper.selectOrderDetail(orderId);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            return order;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取订单详情失败", e);
            throw new BusinessException("获取订单详情失败");
        }
    }

    @Override
    public boolean hasPermission(Long userId, Long orderId) {
        if (userId == null || orderId == null) {
            return false;
        }
        
        log.debug("检查用户订单操作权限: userId={}, orderId={}", userId, orderId);
        try {
            AdoptionOrder order = getById(orderId);
            if (order == null) {
                return false;
            }
            
            // 检查订单是否属于该用户
            return order.getUserId().equals(userId);
        } catch (Exception e) {
            log.error("检查用户订单操作权限失败", e);
            return false;
        }
    }

    @Override
    public String generateOrderNo() {
        // 生成格式：AD + yyyyMMddHHmmss + 4位随机数
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "AD" + timestamp + random;
    }

    @Override
    public List<AdoptionOrder> getTimeoutOrders(Integer timeoutMinutes) {
        if (timeoutMinutes == null || timeoutMinutes <= 0) {
            timeoutMinutes = 30; // 默认30分钟超时
        }
        
        log.debug("查询超时未支付的订单，超时时间: {}分钟", timeoutMinutes);
        try {
            LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(timeoutMinutes);
            List<AdoptionOrder> orders = adoptionOrderMapper.selectTimeoutOrders(timeoutTime);
            log.debug("查询到{}个超时订单", orders.size());
            return orders;
        } catch (Exception e) {
            log.error("查询超时订单失败", e);
            throw new BusinessException("查询超时订单失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int autoCancelTimeoutOrders(Integer timeoutMinutes) {
        log.info("自动取消超时订单，超时时间: {}分钟", timeoutMinutes);
        try {
            List<AdoptionOrder> timeoutOrders = getTimeoutOrders(timeoutMinutes);
            int cancelledCount = 0;
            
            for (AdoptionOrder order : timeoutOrders) {
                try {
                    // 更新订单状态为已取消
                    AdoptionOrder updateOrder = new AdoptionOrder();
                    updateOrder.setId(order.getId());
                    updateOrder.setOrderStatus(4); // 已取消状态
                    
                    if (updateById(updateOrder)) {
                        // 释放预占用的项目单元
                        adoptionProjectService.updateAvailableUnits(order.getProjectId(), order.getUnitCount());
                        cancelledCount++;
                        
                        log.info("自动取消超时订单成功: {}", order.getOrderNo());
                    }
                } catch (Exception e) {
                    log.error("自动取消订单失败: {}", order.getOrderNo(), e);
                }
            }
            
            log.info("自动取消超时订单完成，取消{}个订单", cancelledCount);
            return cancelledCount;
        } catch (Exception e) {
            log.error("自动取消超时订单失败", e);
            throw new BusinessException("自动取消超时订单失败");
        }
    }

    @Override
    public int countUserOrders(Long userId, Integer orderStatus) {
        if (userId == null) {
            return 0;
        }
        
        return adoptionOrderMapper.countUserOrders(userId, orderStatus);
    }

    @Override
    public BigDecimal sumUserOrderAmount(Long userId, Integer orderStatus) {
        if (userId == null) {
            return BigDecimal.ZERO;
        }
        
        log.debug("统计用户订单金额: userId={}, orderStatus={}", userId, orderStatus);
        try {
            LambdaQueryWrapper<AdoptionOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdoptionOrder::getUserId, userId);
            if (orderStatus != null) {
                wrapper.eq(AdoptionOrder::getOrderStatus, orderStatus);
            }
            wrapper.select(AdoptionOrder::getActualAmount);
            
            List<AdoptionOrder> orders = list(wrapper);
            BigDecimal totalAmount = orders.stream()
                    .map(AdoptionOrder::getActualAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            log.debug("用户{}订单总金额: {}", userId, totalAmount);
            return totalAmount;
        } catch (Exception e) {
            log.error("统计用户订单金额失败", e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public Object getOrderStatistics(Long userId) {
        log.debug("获取订单统计信息，用户ID: {}", userId);
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            if (userId != null) {
                // 特定用户的统计
                statistics.put("totalCount", countUserOrders(userId, null));
                statistics.put("pendingCount", countUserOrders(userId, 1));
                statistics.put("paidCount", countUserOrders(userId, 2));
                statistics.put("completedCount", countUserOrders(userId, 3));
                statistics.put("cancelledCount", countUserOrders(userId, 4));
                statistics.put("refundedCount", countUserOrders(userId, 5));
                
                statistics.put("totalAmount", sumUserOrderAmount(userId, null));
                statistics.put("paidAmount", sumUserOrderAmount(userId, 2));
            } else {
                // 全局统计
                statistics.put("totalCount", count());
                statistics.put("pendingCount", count(new LambdaQueryWrapper<AdoptionOrder>().eq(AdoptionOrder::getOrderStatus, 1)));
                statistics.put("paidCount", count(new LambdaQueryWrapper<AdoptionOrder>().eq(AdoptionOrder::getOrderStatus, 2)));
                statistics.put("completedCount", count(new LambdaQueryWrapper<AdoptionOrder>().eq(AdoptionOrder::getOrderStatus, 3)));
                statistics.put("cancelledCount", count(new LambdaQueryWrapper<AdoptionOrder>().eq(AdoptionOrder::getOrderStatus, 4)));
                statistics.put("refundedCount", count(new LambdaQueryWrapper<AdoptionOrder>().eq(AdoptionOrder::getOrderStatus, 5)));
            }
            
            return statistics;
        } catch (Exception e) {
            log.error("获取订单统计信息失败", e);
            throw new BusinessException("获取订单统计信息失败");
        }
    }

    @Override
    public Object calculateOrderAmount(Long projectId, Integer unitCount) {
        if (projectId == null || unitCount == null || unitCount <= 0) {
            throw new BusinessException("参数无效");
        }
        
        log.debug("计算订单金额: projectId={}, unitCount={}", projectId, unitCount);
        try {
            // 获取项目信息
            AdoptionProject project = adoptionProjectService.getById(projectId);
            if (project == null) {
                throw new BusinessException("项目不存在");
            }
            
            BigDecimal unitPrice = project.getUnitPrice();
            BigDecimal totalAmount = unitPrice.multiply(new BigDecimal(unitCount));
            
            // 计算优惠金额（这里可以根据业务规则计算）
            BigDecimal discountAmount = BigDecimal.ZERO;
            
            // TODO: 根据用户等级、活动等计算优惠
            
            BigDecimal actualAmount = totalAmount.subtract(discountAmount);
            
            Map<String, Object> result = new HashMap<>();
            result.put("unitPrice", unitPrice);
            result.put("unitCount", unitCount);
            result.put("totalAmount", totalAmount);
            result.put("discountAmount", discountAmount);
            result.put("actualAmount", actualAmount);
            
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("计算订单金额失败", e);
            throw new BusinessException("计算订单金额失败");
        }
    }

    @Override
    public boolean verifyPaymentCallback(String orderNo, String paymentNo, BigDecimal amount) {
        if (!StringUtils.hasText(orderNo) || !StringUtils.hasText(paymentNo) || amount == null) {
            throw new BusinessException("参数不能为空");
        }
        
        log.info("验证支付回调: orderNo={}, paymentNo={}, amount={}", orderNo, paymentNo, amount);
        try {
            // 获取订单信息
            AdoptionOrder order = getOrderByOrderNo(orderNo);
            if (order == null) {
                log.error("订单不存在: {}", orderNo);
                return false;
            }
            
            // 验证订单状态
            if (order.getOrderStatus() != 1) {
                log.error("订单状态不正确: {}, 当前状态: {}", orderNo, order.getOrderStatus());
                return false;
            }
            
            // 验证金额
            if (order.getActualAmount().compareTo(amount) != 0) {
                log.error("支付金额不匹配: {}, 订单金额: {}, 支付金额: {}", orderNo, order.getActualAmount(), amount);
                return false;
            }
            
            log.info("支付回调验证成功: {}", orderNo);
            return true;
        } catch (Exception e) {
            log.error("验证支付回调失败", e);
            return false;
        }
    }
}
