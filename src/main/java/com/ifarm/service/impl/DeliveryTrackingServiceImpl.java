package com.ifarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifarm.common.exception.BusinessException;
import com.ifarm.entity.DeliveryTracking;
import com.ifarm.mapper.DeliveryTrackingMapper;
import com.ifarm.service.IDeliveryTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

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
    public List<DeliveryTracking> getTrackingsByDeliveryOrderId(Long deliveryOrderId) {
        if (deliveryOrderId == null) {
            throw new BusinessException("配送订单ID不能为空");
        }
        
        log.debug("根据配送订单ID查询跟踪记录列表: {}", deliveryOrderId);
        try {
            List<DeliveryTracking> trackings = deliveryTrackingMapper.selectByDeliveryOrderId(deliveryOrderId);
            log.debug("查询到{}条跟踪记录", trackings.size());
            return trackings;
        } catch (Exception e) {
            log.error("根据配送订单ID查询跟踪记录列表失败，订单ID: {}", deliveryOrderId, e);
            throw new BusinessException("查询跟踪记录列表失败");
        }
    }

    @Override
    public DeliveryTracking getLatestTrackingByDeliveryOrderId(Long deliveryOrderId) {
        if (deliveryOrderId == null) {
            throw new BusinessException("配送订单ID不能为空");
        }
        
        log.debug("根据配送订单ID查询最新跟踪记录: {}", deliveryOrderId);
        try {
            return deliveryTrackingMapper.selectLatestByDeliveryOrderId(deliveryOrderId);
        } catch (Exception e) {
            log.error("根据配送订单ID查询最新跟踪记录失败，订单ID: {}", deliveryOrderId, e);
            throw new BusinessException("查询最新跟踪记录失败");
        }
    }

    @Override
    public List<DeliveryTracking> getTrackingsByStatus(String trackingStatus) {
        if (!StringUtils.hasText(trackingStatus)) {
            throw new BusinessException("跟踪状态不能为空");
        }
        
        log.debug("根据跟踪状态查询记录列表: {}", trackingStatus);
        try {
            List<DeliveryTracking> trackings = deliveryTrackingMapper.selectByTrackingStatus(trackingStatus);
            log.debug("查询到{}条状态为{}的跟踪记录", trackings.size(), trackingStatus);
            return trackings;
        } catch (Exception e) {
            log.error("根据跟踪状态查询记录列表失败，状态: {}", trackingStatus, e);
            throw new BusinessException("查询跟踪记录列表失败");
        }
    }

    @Override
    public List<DeliveryTracking> getTrackingsByOperator(String operator) {
        if (!StringUtils.hasText(operator)) {
            throw new BusinessException("操作人不能为空");
        }
        
        log.debug("根据操作人查询跟踪记录列表: {}", operator);
        try {
            List<DeliveryTracking> trackings = deliveryTrackingMapper.selectByOperator(operator);
            log.debug("查询到{}条操作人为{}的跟踪记录", trackings.size(), operator);
            return trackings;
        } catch (Exception e) {
            log.error("根据操作人查询跟踪记录列表失败，操作人: {}", operator, e);
            throw new BusinessException("查询跟踪记录列表失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createTracking(Long deliveryOrderId, String trackingStatus, String trackingInfo, 
                                String location, String operator, LocalDateTime trackingTime) {
        if (deliveryOrderId == null || !StringUtils.hasText(trackingStatus)) {
            throw new BusinessException("配送订单ID和跟踪状态不能为空");
        }
        
        log.info("创建跟踪记录: 订单ID={}, 状态={}, 操作人={}", deliveryOrderId, trackingStatus, operator);
        try {
            DeliveryTracking tracking = new DeliveryTracking();
            tracking.setDeliveryOrderId(deliveryOrderId);
            tracking.setTrackingStatus(trackingStatus);
            tracking.setTrackingInfo(trackingInfo);
            tracking.setLocation(location);
            tracking.setOperator(operator);
            tracking.setTrackingTime(trackingTime != null ? trackingTime : LocalDateTime.now());
            
            boolean result = save(tracking);
            if (result) {
                log.info("跟踪记录创建成功，ID: {}", tracking.getId());
            } else {
                log.error("跟踪记录创建失败");
                throw new BusinessException("跟踪记录创建失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建跟踪记录失败", e);
            throw new BusinessException("创建跟踪记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCreateTrackings(List<DeliveryTracking> trackingList) {
        if (trackingList == null || trackingList.isEmpty()) {
            throw new BusinessException("跟踪记录列表不能为空");
        }
        
        log.info("批量创建跟踪记录，数量: {}", trackingList.size());
        try {
            // 验证每条记录并设置默认时间
            for (DeliveryTracking tracking : trackingList) {
                if (tracking.getDeliveryOrderId() == null || !StringUtils.hasText(tracking.getTrackingStatus())) {
                    throw new BusinessException("跟踪记录信息不完整");
                }
                
                if (tracking.getTrackingTime() == null) {
                    tracking.setTrackingTime(LocalDateTime.now());
                }
            }
            
            boolean result = saveBatch(trackingList);
            if (result) {
                log.info("批量创建跟踪记录成功，创建{}条记录", trackingList.size());
            } else {
                log.error("批量创建跟踪记录失败");
                throw new BusinessException("批量创建跟踪记录失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量创建跟踪记录失败", e);
            throw new BusinessException("批量创建跟踪记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTracking(DeliveryTracking deliveryTracking) {
        if (deliveryTracking == null || deliveryTracking.getId() == null) {
            throw new BusinessException("跟踪记录信息不完整");
        }
        
        log.info("更新跟踪记录: ID={}", deliveryTracking.getId());
        try {
            // 验证记录是否存在
            DeliveryTracking existingTracking = getById(deliveryTracking.getId());
            if (existingTracking == null) {
                throw new BusinessException("跟踪记录不存在");
            }
            
            boolean result = updateById(deliveryTracking);
            if (result) {
                log.info("跟踪记录更新成功");
            } else {
                log.error("跟踪记录更新失败");
                throw new BusinessException("跟踪记录更新失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新跟踪记录失败", e);
            throw new BusinessException("更新跟踪记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTracking(Long trackingId) {
        if (trackingId == null) {
            throw new BusinessException("跟踪记录ID不能为空");
        }
        
        log.info("删除跟踪记录: ID={}", trackingId);
        try {
            // 验证记录是否存在
            DeliveryTracking tracking = getById(trackingId);
            if (tracking == null) {
                throw new BusinessException("跟踪记录不存在");
            }
            
            boolean result = removeById(trackingId);
            if (result) {
                log.info("跟踪记录删除成功");
            } else {
                log.error("跟踪记录删除失败");
                throw new BusinessException("跟踪记录删除失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除跟踪记录失败", e);
            throw new BusinessException("删除跟踪记录失败");
        }
    }

    @Override
    public DeliveryTracking getTrackingDetail(Long trackingId) {
        if (trackingId == null) {
            throw new BusinessException("跟踪记录ID不能为空");
        }
        
        log.debug("获取跟踪记录详情: ID={}", trackingId);
        try {
            DeliveryTracking tracking = getById(trackingId);
            if (tracking == null) {
                throw new BusinessException("跟踪记录不存在");
            }
            return tracking;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取跟踪记录详情失败", e);
            throw new BusinessException("获取跟踪记录详情失败");
        }
    }

    @Override
    public int countTrackingsByDeliveryOrderId(Long deliveryOrderId) {
        if (deliveryOrderId == null) {
            return 0;
        }
        
        return deliveryTrackingMapper.countByDeliveryOrderId(deliveryOrderId);
    }

    @Override
    public List<DeliveryTracking> getTrackingTimeline(Long deliveryOrderId) {
        if (deliveryOrderId == null) {
            throw new BusinessException("配送订单ID不能为空");
        }
        
        log.debug("获取配送订单跟踪时间线: deliveryOrderId={}", deliveryOrderId);
        try {
            LambdaQueryWrapper<DeliveryTracking> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DeliveryTracking::getDeliveryOrderId, deliveryOrderId);
            wrapper.orderByAsc(DeliveryTracking::getTrackingTime);
            
            List<DeliveryTracking> trackings = list(wrapper);
            log.debug("获取到{}条跟踪记录", trackings.size());
            return trackings;
        } catch (Exception e) {
            log.error("获取配送订单跟踪时间线失败", e);
            throw new BusinessException("获取跟踪时间线失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addShipmentTracking(Long deliveryOrderId, String location, String operator) {
        if (deliveryOrderId == null) {
            throw new BusinessException("配送订单ID不能为空");
        }
        
        log.info("添加发货跟踪记录: deliveryOrderId={}, location={}, operator={}", deliveryOrderId, location, operator);
        try {
            return createTracking(deliveryOrderId, "已发货", 
                String.format("商品已从%s发出", location != null ? location : "仓库"), 
                location, operator, LocalDateTime.now());
        } catch (Exception e) {
            log.error("添加发货跟踪记录失败", e);
            throw new BusinessException("添加发货跟踪记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addInTransitTracking(Long deliveryOrderId, String location, String trackingInfo) {
        if (deliveryOrderId == null) {
            throw new BusinessException("配送订单ID不能为空");
        }
        
        log.info("添加运输中跟踪记录: deliveryOrderId={}, location={}", deliveryOrderId, location);
        try {
            return createTracking(deliveryOrderId, "运输中", 
                trackingInfo != null ? trackingInfo : "商品正在运输中", 
                location, "物流系统", LocalDateTime.now());
        } catch (Exception e) {
            log.error("添加运输中跟踪记录失败", e);
            throw new BusinessException("添加运输中跟踪记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addOutForDeliveryTracking(Long deliveryOrderId, String location, String operator) {
        if (deliveryOrderId == null) {
            throw new BusinessException("配送订单ID不能为空");
        }
        
        log.info("添加派送中跟踪记录: deliveryOrderId={}, location={}, operator={}", deliveryOrderId, location, operator);
        try {
            return createTracking(deliveryOrderId, "派送中", 
                String.format("商品已到达%s，正在派送中", location != null ? location : "派送站点"), 
                location, operator, LocalDateTime.now());
        } catch (Exception e) {
            log.error("添加派送中跟踪记录失败", e);
            throw new BusinessException("添加派送中跟踪记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addDeliveredTracking(Long deliveryOrderId, String location, String operator) {
        if (deliveryOrderId == null) {
            throw new BusinessException("配送订单ID不能为空");
        }
        
        log.info("添加签收跟踪记录: deliveryOrderId={}, location={}, operator={}", deliveryOrderId, location, operator);
        try {
            return createTracking(deliveryOrderId, "已签收", 
                String.format("商品已在%s签收", location != null ? location : "收货地址"), 
                location, operator, LocalDateTime.now());
        } catch (Exception e) {
            log.error("添加签收跟踪记录失败", e);
            throw new BusinessException("添加签收跟踪记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addExceptionTracking(Long deliveryOrderId, String exceptionInfo, String location, String operator) {
        if (deliveryOrderId == null || !StringUtils.hasText(exceptionInfo)) {
            throw new BusinessException("配送订单ID和异常信息不能为空");
        }
        
        log.info("添加异常跟踪记录: deliveryOrderId={}, exceptionInfo={}", deliveryOrderId, exceptionInfo);
        try {
            return createTracking(deliveryOrderId, "配送异常", exceptionInfo, location, operator, LocalDateTime.now());
        } catch (Exception e) {
            log.error("添加异常跟踪记录失败", e);
            throw new BusinessException("添加异常跟踪记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean syncTrackingFromAPI(Long deliveryOrderId, String trackingNumber, String logisticsCompany) {
        if (deliveryOrderId == null || !StringUtils.hasText(trackingNumber) || !StringUtils.hasText(logisticsCompany)) {
            throw new BusinessException("参数不能为空");
        }
        
        log.info("从第三方API同步跟踪信息: deliveryOrderId={}, trackingNumber={}, logisticsCompany={}", 
                deliveryOrderId, trackingNumber, logisticsCompany);
        try {
            // TODO: 调用第三方物流API获取跟踪信息
            // 这里模拟同步过程
            List<Map<String, Object>> apiTrackings = mockGetAPITrackings(trackingNumber, logisticsCompany);
            
            if (apiTrackings.isEmpty()) {
                log.warn("未获取到第三方物流跟踪信息: {}", trackingNumber);
                return false;
            }
            
            // 添加新的跟踪记录
            for (Map<String, Object> apiTracking : apiTrackings) {
                String status = (String) apiTracking.get("status");
                String info = (String) apiTracking.get("info");
                String location = (String) apiTracking.get("location");
                LocalDateTime time = (LocalDateTime) apiTracking.get("time");
                
                // 检查是否已存在相同的跟踪记录
                if (!existsTrackingRecord(deliveryOrderId, status, time)) {
                    createTracking(deliveryOrderId, status, info, location, "物流系统", time);
                }
            }
            
            log.info("从第三方API同步跟踪信息成功");
            return true;
        } catch (Exception e) {
            log.error("从第三方API同步跟踪信息失败", e);
            throw new BusinessException("同步跟踪信息失败");
        }
    }

    @Override
    public List<String> getTrackingStatuses() {
        log.debug("获取跟踪状态列表");
        // 返回常用的跟踪状态列表
        return Arrays.asList(
            "订单已创建", "已发货", "运输中", "到达中转站", 
            "派送中", "已签收", "配送异常", "退回中"
        );
    }

    @Override
    public boolean hasNewTrackingUpdates(Long deliveryOrderId, LocalDateTime lastCheckTime) {
        if (deliveryOrderId == null || lastCheckTime == null) {
            return false;
        }
        
        log.debug("检查是否有新的跟踪更新: deliveryOrderId={}, lastCheckTime={}", deliveryOrderId, lastCheckTime);
        try {
            LambdaQueryWrapper<DeliveryTracking> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DeliveryTracking::getDeliveryOrderId, deliveryOrderId);
            wrapper.gt(DeliveryTracking::getTrackingTime, lastCheckTime);
            
            return count(wrapper) > 0;
        } catch (Exception e) {
            log.error("检查是否有新的跟踪更新失败", e);
            return false;
        }
    }

    @Override
    public List<DeliveryTracking> getUserTrackingInfo(Long userId, Long deliveryOrderId) {
        if (userId == null || deliveryOrderId == null) {
            throw new BusinessException("参数不能为空");
        }
        
        log.debug("获取用户的跟踪信息: userId={}, deliveryOrderId={}", userId, deliveryOrderId);
        try {
            // TODO: 验证用户权限，确保用户只能查看自己的订单跟踪信息
            // 这里简化处理，直接返回跟踪信息
            return getTrackingsByDeliveryOrderId(deliveryOrderId);
        } catch (Exception e) {
            log.error("获取用户的跟踪信息失败", e);
            throw new BusinessException("获取跟踪信息失败");
        }
    }

    @Override
    public Object formatTrackingInfo(Long deliveryOrderId) {
        if (deliveryOrderId == null) {
            throw new BusinessException("配送订单ID不能为空");
        }
        
        log.debug("格式化跟踪信息: deliveryOrderId={}", deliveryOrderId);
        try {
            List<DeliveryTracking> trackings = getTrackingTimeline(deliveryOrderId);
            
            Map<String, Object> formattedInfo = new HashMap<>();
            formattedInfo.put("orderId", deliveryOrderId);
            formattedInfo.put("totalRecords", trackings.size());
            
            if (!trackings.isEmpty()) {
                DeliveryTracking latest = trackings.get(trackings.size() - 1);
                formattedInfo.put("currentStatus", latest.getTrackingStatus());
                formattedInfo.put("currentInfo", latest.getTrackingInfo());
                formattedInfo.put("currentLocation", latest.getLocation());
                formattedInfo.put("lastUpdateTime", latest.getTrackingTime());
            }
            
            // 格式化时间线
            List<Map<String, Object>> timeline = new ArrayList<>();
            for (DeliveryTracking tracking : trackings) {
                Map<String, Object> item = new HashMap<>();
                item.put("status", tracking.getTrackingStatus());
                item.put("info", tracking.getTrackingInfo());
                item.put("location", tracking.getLocation());
                item.put("operator", tracking.getOperator());
                item.put("time", tracking.getTrackingTime());
                timeline.add(item);
            }
            formattedInfo.put("timeline", timeline);
            
            return formattedInfo;
        } catch (Exception e) {
            log.error("格式化跟踪信息失败", e);
            throw new BusinessException("格式化跟踪信息失败");
        }
    }

    @Override
    public LocalDateTime predictDeliveryTime(Long deliveryOrderId) {
        if (deliveryOrderId == null) {
            throw new BusinessException("配送订单ID不能为空");
        }
        
        log.debug("预测配送时间: deliveryOrderId={}", deliveryOrderId);
        try {
            // TODO: 根据历史数据和当前状态预测配送时间
            // 这里简化处理，返回当前时间加2天
            return LocalDateTime.now().plusDays(2);
        } catch (Exception e) {
            log.error("预测配送时间失败", e);
            return null;
        }
    }

    /**
     * 检查是否存在相同的跟踪记录
     */
    private boolean existsTrackingRecord(Long deliveryOrderId, String status, LocalDateTime time) {
        LambdaQueryWrapper<DeliveryTracking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryTracking::getDeliveryOrderId, deliveryOrderId);
        wrapper.eq(DeliveryTracking::getTrackingStatus, status);
        wrapper.eq(DeliveryTracking::getTrackingTime, time);
        
        return count(wrapper) > 0;
    }

    /**
     * 模拟获取第三方API跟踪信息
     * 实际实现中应该调用第三方物流API
     */
    private List<Map<String, Object>> mockGetAPITrackings(String trackingNumber, String logisticsCompany) {
        // TODO: 实际实现中调用第三方物流API
        // 这里返回模拟数据
        return List.of(
            Map.of(
                "status", "运输中",
                "info", "快件已到达中转站",
                "location", "北京中转站",
                "time", LocalDateTime.now().minusHours(2)
            ),
            Map.of(
                "status", "派送中",
                "info", "快件正在派送中",
                "location", "目标城市派送站",
                "time", LocalDateTime.now().minusHours(1)
            )
        );
    }
}
