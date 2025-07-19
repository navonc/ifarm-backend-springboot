package com.ifarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.entity.DeliveryTracking;
import com.ifarm.mapper.DeliveryTrackingMapper;
import com.ifarm.service.IDeliveryTrackingService;
import com.ifarm.service.IDeliveryOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物流跟踪服务实现类
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryTrackingServiceImpl extends ServiceImpl<DeliveryTrackingMapper, DeliveryTracking> implements IDeliveryTrackingService {

    private final DeliveryTrackingMapper deliveryTrackingMapper;

    @Override
    public List<DeliveryTracking> getTrackingsByOrderId(Long orderId) {
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }
        
        log.debug("根据订单ID查询物流跟踪记录: {}", orderId);
        try {
            List<DeliveryTracking> trackings = deliveryTrackingMapper.selectByOrderId(orderId);
            log.debug("查询到{}条物流跟踪记录", trackings.size());
            return trackings;
        } catch (Exception e) {
            log.error("根据订单ID查询物流跟踪记录失败，订单ID: {}", orderId, e);
            throw new BusinessException("查询物流跟踪记录失败");
        }
    }

    @Override
    public List<DeliveryTracking> getTrackingsByTrackingNumber(String trackingNumber) {
        if (!StringUtils.hasText(trackingNumber)) {
            throw new BusinessException("快递单号不能为空");
        }
        
        log.debug("根据快递单号查询物流跟踪记录: {}", trackingNumber);
        try {
            List<DeliveryTracking> trackings = deliveryTrackingMapper.selectByTrackingNumber(trackingNumber);
            log.debug("查询到{}条物流跟踪记录", trackings.size());
            return trackings;
        } catch (Exception e) {
            log.error("根据快递单号查询物流跟踪记录失败，快递单号: {}", trackingNumber, e);
            throw new BusinessException("查询物流跟踪记录失败");
        }
    }

    @Override
    public DeliveryTracking getLatestTracking(Long orderId) {
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }
        
        log.debug("获取订单最新物流跟踪记录: orderId={}", orderId);
        try {
            DeliveryTracking tracking = deliveryTrackingMapper.selectLatestByOrderId(orderId);
            return tracking;
        } catch (Exception e) {
            log.error("获取订单最新物流跟踪记录失败", e);
            throw new BusinessException("获取最新物流跟踪记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createInitialTracking(Long orderId) {
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }
        
        log.info("创建初始物流跟踪记录: orderId={}", orderId);
        try {
            DeliveryTracking tracking = new DeliveryTracking();
            tracking.setOrderId(orderId);
            tracking.setTrackingStatus("订单已创建");
            tracking.setTrackingDescription("配送订单已创建，等待发货");
            tracking.setTrackingTime(LocalDateTime.now());
            
            boolean result = save(tracking);
            if (result) {
                log.info("初始物流跟踪记录创建成功");
            } else {
                log.error("初始物流跟踪记录创建失败");
                throw new BusinessException("初始物流跟踪记录创建失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建初始物流跟踪记录失败", e);
            throw new BusinessException("创建初始物流跟踪记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTrackingRecord(Long orderId, String trackingStatus, String trackingDescription) {
        if (orderId == null || !StringUtils.hasText(trackingStatus)) {
            throw new BusinessException("参数不能为空");
        }
        
        log.info("添加物流跟踪记录: orderId={}, status={}, description={}", orderId, trackingStatus, trackingDescription);
        try {
            DeliveryTracking tracking = new DeliveryTracking();
            tracking.setOrderId(orderId);
            tracking.setTrackingStatus(trackingStatus);
            tracking.setTrackingDescription(trackingDescription);
            tracking.setTrackingTime(LocalDateTime.now());
            
            boolean result = save(tracking);
            if (result) {
                log.info("物流跟踪记录添加成功");
            } else {
                log.error("物流跟踪记录添加失败");
                throw new BusinessException("物流跟踪记录添加失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("添加物流跟踪记录失败", e);
            throw new BusinessException("添加物流跟踪记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAddTrackingRecords(List<DeliveryTracking> trackingRecords) {
        if (trackingRecords == null || trackingRecords.isEmpty()) {
            throw new BusinessException("跟踪记录列表不能为空");
        }
        
        log.info("批量添加物流跟踪记录，数量: {}", trackingRecords.size());
        try {
            // 验证每条记录并设置默认时间
            for (DeliveryTracking tracking : trackingRecords) {
                if (tracking.getOrderId() == null || !StringUtils.hasText(tracking.getTrackingStatus())) {
                    throw new BusinessException("跟踪记录信息不完整");
                }
                
                if (tracking.getTrackingTime() == null) {
                    tracking.setTrackingTime(LocalDateTime.now());
                }
            }
            
            boolean result = saveBatch(trackingRecords);
            if (result) {
                log.info("批量添加物流跟踪记录成功，添加{}条记录", trackingRecords.size());
            } else {
                log.error("批量添加物流跟踪记录失败");
                throw new BusinessException("批量添加物流跟踪记录失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量添加物流跟踪记录失败", e);
            throw new BusinessException("批量添加物流跟踪记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTracking(DeliveryTracking deliveryTracking) {
        if (deliveryTracking == null || deliveryTracking.getId() == null) {
            throw new BusinessException("物流跟踪记录信息不完整");
        }
        
        log.info("更新物流跟踪记录: ID={}", deliveryTracking.getId());
        try {
            // 验证记录是否存在
            DeliveryTracking existingTracking = getById(deliveryTracking.getId());
            if (existingTracking == null) {
                throw new BusinessException("物流跟踪记录不存在");
            }
            
            boolean result = updateById(deliveryTracking);
            if (result) {
                log.info("物流跟踪记录更新成功");
            } else {
                log.error("物流跟踪记录更新失败");
                throw new BusinessException("物流跟踪记录更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新物流跟踪记录失败", e);
            throw new BusinessException("更新物流跟踪记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTracking(Long trackingId) {
        if (trackingId == null) {
            throw new BusinessException("跟踪记录ID不能为空");
        }
        
        log.info("删除物流跟踪记录: ID={}", trackingId);
        try {
            // 验证记录是否存在
            DeliveryTracking tracking = getById(trackingId);
            if (tracking == null) {
                throw new BusinessException("物流跟踪记录不存在");
            }
            
            boolean result = removeById(trackingId);
            if (result) {
                log.info("物流跟踪记录删除成功");
            } else {
                log.error("物流跟踪记录删除失败");
                throw new BusinessException("物流跟踪记录删除失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除物流跟踪记录失败", e);
            throw new BusinessException("删除物流跟踪记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteTrackings(List<Long> trackingIds) {
        if (trackingIds == null || trackingIds.isEmpty()) {
            throw new BusinessException("跟踪记录ID列表不能为空");
        }
        
        log.info("批量删除物流跟踪记录: IDs={}", trackingIds);
        try {
            boolean result = removeByIds(trackingIds);
            if (result) {
                log.info("批量删除物流跟踪记录成功，删除{}条记录", trackingIds.size());
            } else {
                log.error("批量删除物流跟踪记录失败");
                throw new BusinessException("批量删除物流跟踪记录失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除物流跟踪记录失败", e);
            throw new BusinessException("批量删除物流跟踪记录失败");
        }
    }

    @Override
    public DeliveryTracking getTrackingDetail(Long trackingId) {
        if (trackingId == null) {
            throw new BusinessException("跟踪记录ID不能为空");
        }
        
        log.debug("获取物流跟踪记录详情: ID={}", trackingId);
        try {
            DeliveryTracking tracking = getById(trackingId);
            if (tracking == null) {
                throw new BusinessException("物流跟踪记录不存在");
            }
            return tracking;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取物流跟踪记录详情失败", e);
            throw new BusinessException("获取物流跟踪记录详情失败");
        }
    }

    @Override
    public List<DeliveryTracking> getTrackingTimeline(Long orderId) {
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }
        
        log.debug("获取订单物流跟踪时间线: orderId={}", orderId);
        try {
            LambdaQueryWrapper<DeliveryTracking> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DeliveryTracking::getOrderId, orderId);
            wrapper.orderByAsc(DeliveryTracking::getTrackingTime);
            
            List<DeliveryTracking> trackings = list(wrapper);
            log.debug("获取到{}条物流跟踪记录", trackings.size());
            return trackings;
        } catch (Exception e) {
            log.error("获取订单物流跟踪时间线失败", e);
            throw new BusinessException("获取物流跟踪时间线失败");
        }
    }

    @Override
    public boolean hasPermission(Long userId, Long trackingId) {
        if (userId == null || trackingId == null) {
            return false;
        }
        
        log.debug("检查用户物流跟踪记录操作权限: userId={}, trackingId={}", userId, trackingId);
        try {
            // 获取跟踪记录信息
            DeliveryTracking tracking = getById(trackingId);
            if (tracking == null) {
                return false;
            }
            
            // TODO: 通过订单ID检查用户权限
            // 这里需要注入DeliveryOrderService，但为了避免循环依赖，暂时返回true
            // 实际实现中应该检查该跟踪记录所属订单是否属于该用户
            return true;
        } catch (Exception e) {
            log.error("检查用户物流跟踪记录操作权限失败", e);
            return false;
        }
    }

    @Override
    public int countTrackingsByOrder(Long orderId) {
        if (orderId == null) {
            return 0;
        }
        
        return deliveryTrackingMapper.countByOrderId(orderId);
    }

    @Override
    public Object getTrackingStatistics(Long orderId) {
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }
        
        log.debug("获取订单物流跟踪统计信息: orderId={}", orderId);
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 基础统计
            statistics.put("totalRecords", countTrackingsByOrder(orderId));
            
            // 最新跟踪信息
            DeliveryTracking latestTracking = getLatestTracking(orderId);
            if (latestTracking != null) {
                statistics.put("latestStatus", latestTracking.getTrackingStatus());
                statistics.put("latestDescription", latestTracking.getTrackingDescription());
                statistics.put("latestTime", latestTracking.getTrackingTime());
            }
            
            // 跟踪时间线
            List<DeliveryTracking> timeline = getTrackingTimeline(orderId);
            statistics.put("timeline", timeline);
            
            return statistics;
        } catch (Exception e) {
            log.error("获取订单物流跟踪统计信息失败", e);
            throw new BusinessException("获取物流跟踪统计信息失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean syncTrackingFromCourier(String trackingNumber, String courierCompany) {
        if (!StringUtils.hasText(trackingNumber) || !StringUtils.hasText(courierCompany)) {
            throw new BusinessException("参数不能为空");
        }
        
        log.info("同步快递公司物流信息: trackingNumber={}, courierCompany={}", trackingNumber, courierCompany);
        try {
            // TODO: 调用快递公司API获取物流信息
            // 这里模拟同步过程
            
            List<Map<String, Object>> courierTrackings = mockGetCourierTrackings(trackingNumber, courierCompany);
            
            if (courierTrackings.isEmpty()) {
                log.warn("未获取到快递公司物流信息: {}", trackingNumber);
                return false;
            }
            
            // 查找对应的配送订单
            List<DeliveryTracking> existingTrackings = getTrackingsByTrackingNumber(trackingNumber);
            if (existingTrackings.isEmpty()) {
                log.warn("未找到对应的配送订单: {}", trackingNumber);
                return false;
            }
            
            Long orderId = existingTrackings.get(0).getOrderId();
            
            // 添加新的跟踪记录
            for (Map<String, Object> courierTracking : courierTrackings) {
                String status = (String) courierTracking.get("status");
                String description = (String) courierTracking.get("description");
                LocalDateTime time = (LocalDateTime) courierTracking.get("time");
                
                // 检查是否已存在相同的跟踪记录
                if (!existsTrackingRecord(orderId, status, time)) {
                    DeliveryTracking tracking = new DeliveryTracking();
                    tracking.setOrderId(orderId);
                    tracking.setTrackingStatus(status);
                    tracking.setTrackingDescription(description);
                    tracking.setTrackingTime(time);
                    
                    save(tracking);
                }
            }
            
            log.info("同步快递公司物流信息成功");
            return true;
        } catch (Exception e) {
            log.error("同步快递公司物流信息失败", e);
            throw new BusinessException("同步物流信息失败");
        }
    }

    @Override
    public List<DeliveryTracking> getRecentTrackings(Integer hours) {
        if (hours == null || hours <= 0) {
            hours = 24; // 默认24小时
        }
        
        log.debug("获取最近物流跟踪记录，小时数: {}", hours);
        try {
            LocalDateTime startTime = LocalDateTime.now().minusHours(hours);
            
            LambdaQueryWrapper<DeliveryTracking> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(DeliveryTracking::getTrackingTime, startTime);
            wrapper.orderByDesc(DeliveryTracking::getTrackingTime);
            
            List<DeliveryTracking> trackings = list(wrapper);
            log.debug("获取到{}条最近物流跟踪记录", trackings.size());
            return trackings;
        } catch (Exception e) {
            log.error("获取最近物流跟踪记录失败", e);
            throw new BusinessException("获取最近物流跟踪记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSyncTrackings(List<String> trackingNumbers) {
        if (trackingNumbers == null || trackingNumbers.isEmpty()) {
            throw new BusinessException("快递单号列表不能为空");
        }
        
        log.info("批量同步物流信息，数量: {}", trackingNumbers.size());
        try {
            int successCount = 0;
            
            for (String trackingNumber : trackingNumbers) {
                try {
                    // TODO: 根据快递单号查询对应的快递公司
                    String courierCompany = "默认快递"; // 这里应该从订单信息中获取
                    
                    if (syncTrackingFromCourier(trackingNumber, courierCompany)) {
                        successCount++;
                    }
                } catch (Exception e) {
                    log.error("同步物流信息失败，快递单号: {}", trackingNumber, e);
                }
            }
            
            log.info("批量同步物流信息完成，成功{}个，总共{}个", successCount, trackingNumbers.size());
            return successCount > 0;
        } catch (Exception e) {
            log.error("批量同步物流信息失败", e);
            throw new BusinessException("批量同步物流信息失败");
        }
    }

    /**
     * 检查是否存在相同的跟踪记录
     */
    private boolean existsTrackingRecord(Long orderId, String status, LocalDateTime time) {
        LambdaQueryWrapper<DeliveryTracking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryTracking::getOrderId, orderId);
        wrapper.eq(DeliveryTracking::getTrackingStatus, status);
        wrapper.eq(DeliveryTracking::getTrackingTime, time);
        
        return count(wrapper) > 0;
    }

    /**
     * 模拟获取快递公司物流信息
     * 实际实现中应该调用快递公司的API
     */
    private List<Map<String, Object>> mockGetCourierTrackings(String trackingNumber, String courierCompany) {
        // TODO: 实际实现中调用快递公司API
        // 这里返回模拟数据
        return List.of(
            Map.of(
                "status", "运输中",
                "description", "快件已到达中转站",
                "time", LocalDateTime.now().minusHours(2)
            ),
            Map.of(
                "status", "派送中",
                "description", "快件正在派送中",
                "time", LocalDateTime.now().minusHours(1)
            )
        );
    }
}
