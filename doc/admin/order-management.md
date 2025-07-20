# 订单管理模块 API 文档

## 概述

订单管理模块为管理员和农场主提供认养订单的查看、管理、统计等功能。管理员可以查看所有订单，农场主可以查看自己农场相关的订单。

## 认养订单管理

**基础路径**: `/api/adoption-orders` (⚠️ 注意：此模块接口尚未实现)

### 1. 获取订单列表（管理端）

**接口描述**: 获取认养订单列表，支持分页和高级筛选

- **URL**: `GET /api/adoption-orders/admin`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页大小 |
| orderNo | String | 否 | - | 订单号 |
| userId | Long | 否 | - | 用户ID |
| projectId | Long | 否 | - | 项目ID |
| farmId | Long | 否 | - | 农场ID |
| orderStatus | Integer | 否 | - | 订单状态 |
| paymentMethod | String | 否 | - | 支付方式 |
| startDate | String | 否 | - | 开始日期 (YYYY-MM-DD) |
| endDate | String | 否 | - | 结束日期 (YYYY-MM-DD) |
| minAmount | BigDecimal | 否 | - | 最小金额 |
| maxAmount | BigDecimal | 否 | - | 最大金额 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "orderNo": "AD20250119123456",
        "userId": 1,
        "userName": "用户A",
        "userPhone": "13800138000",
        "projectId": 1,
        "projectName": "有机小白菜认养项目",
        "farmId": 1,
        "farmName": "绿野农场",
        "farmOwner": "张农场主",
        "cropName": "小白菜",
        "unitCount": 2,
        "unitPrice": 88.00,
        "totalAmount": 176.00,
        "discountAmount": 0.00,
        "actualAmount": 176.00,
        "orderStatus": 2,
        "orderStatusName": "已支付",
        "paymentMethod": "wechat",
        "paymentTime": "2025-01-19T16:05:00",
        "paymentNo": "wx_pay_123456789",
        "remark": "希望收获新鲜蔬菜",
        "createTime": "2025-01-19T16:00:00",
        "updateTime": "2025-01-19T16:05:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 2. 获取农场订单列表

**接口描述**: 获取指定农场的订单列表

- **URL**: `GET /api/adoption-orders/farm/{farmId}`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| farmId | Long | 是 | 农场ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页大小 |
| orderStatus | Integer | 否 | - | 订单状态 |
| startDate | String | 否 | - | 开始日期 |
| endDate | String | 否 | - | 结束日期 |

- **响应示例**: 同管理端订单列表

### 3. 获取我的农场订单列表

**接口描述**: 获取当前农场主的所有农场订单

- **URL**: `GET /api/adoption-orders/my-farms`
- **权限**: 需要认证（农场主）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**: 同农场订单列表
- **响应示例**: 同管理端订单列表

### 4. 获取订单详情

**接口描述**: 根据ID获取订单详情

- **URL**: `GET /api/adoption-orders/{id}/admin`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 订单ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "orderNo": "AD20250119123456",
    "userId": 1,
    "userName": "用户A",
    "userPhone": "13800138000",
    "userEmail": "user@example.com",
    "projectId": 1,
    "projectName": "有机小白菜认养项目",
    "farmId": 1,
    "farmName": "绿野农场",
    "farmOwner": "张农场主",
    "farmAddress": "浙江省杭州市余杭区良渚街道农场路123号",
    "cropName": "小白菜",
    "unitCount": 2,
    "unitPrice": 88.00,
    "totalAmount": 176.00,
    "discountAmount": 0.00,
    "actualAmount": 176.00,
    "orderStatus": 2,
    "orderStatusName": "已支付",
    "paymentMethod": "wechat",
    "paymentTime": "2025-01-19T16:05:00",
    "paymentNo": "wx_pay_123456789",
    "remark": "希望收获新鲜蔬菜",
    "adoptionRecords": [
      {
        "id": 1,
        "unitId": 1,
        "unitNumber": "A001",
        "adoptionStatus": 2,
        "adoptionStatusName": "种植中"
      },
      {
        "id": 2,
        "unitId": 2,
        "unitNumber": "A002",
        "adoptionStatus": 2,
        "adoptionStatusName": "种植中"
      }
    ],
    "createTime": "2025-01-19T16:00:00",
    "updateTime": "2025-01-19T16:05:00"
  }
}
```

### 5. 更新订单状态

**接口描述**: 更新订单状态（仅管理员）

- **URL**: `PUT /api/adoption-orders/{id}/status`
- **权限**: 需要认证（管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 订单ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderStatus | Integer | 是 | 订单状态：1-待支付，2-已支付，3-已完成，4-已取消，5-已退款 |
| reason | String | 否 | 状态变更原因 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 6. 退款订单

**接口描述**: 处理订单退款（仅管理员）

- **URL**: `PUT /api/adoption-orders/{id}/refund`
- **权限**: 需要认证（管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 订单ID |

- **请求参数**:

```json
{
  "refundAmount": 176.00,        // 退款金额 (必填)
  "refundReason": "项目取消",     // 退款原因 (必填)
  "refundMethod": "original"     // 退款方式：original-原路退回 (可选)
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "退款处理成功",
  "data": {
    "orderId": 1,
    "refundAmount": 176.00,
    "refundTime": "2025-01-19T18:00:00",
    "refundNo": "RF20250119180000"
  }
}
```

### 7. 批量处理订单

**接口描述**: 批量处理订单（状态更新、导出等）

- **URL**: `POST /api/adoption-orders/batch`
- **权限**: 需要认证（管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

```json
{
  "action": "updateStatus",     // 操作类型：updateStatus, export, cancel
  "orderIds": [1, 2, 3],        // 订单ID数组 (必填)
  "params": {                   // 操作参数
    "orderStatus": 4,           // 新状态（updateStatus时需要）
    "reason": "批量取消"        // 操作原因
  }
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "批量操作成功",
  "data": {
    "successCount": 2,
    "failureCount": 1,
    "failureDetails": [
      {
        "orderId": 3,
        "reason": "订单状态不允许修改"
      }
    ]
  }
}
```

## 订单统计分析

**基础路径**: `/api/adoption-orders/statistics`

### 1. 获取订单统计概览

**接口描述**: 获取订单统计概览信息

- **URL**: `GET /api/adoption-orders/statistics/overview`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| farmId | Long | 否 | - | 农场ID（农场主查看自己的数据） |
| startDate | String | 否 | - | 开始日期 |
| endDate | String | 否 | - | 结束日期 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalOrders": 150,
    "totalRevenue": 26400.00,
    "paidOrders": 135,
    "paidRevenue": 23760.00,
    "pendingOrders": 10,
    "pendingRevenue": 1760.00,
    "cancelledOrders": 5,
    "cancelledRevenue": 880.00,
    "averageOrderAmount": 176.00,
    "conversionRate": 90.0,
    "todayOrders": 8,
    "todayRevenue": 1408.00,
    "yesterdayOrders": 6,
    "yesterdayRevenue": 1056.00,
    "growthRate": 33.3
  }
}
```

### 2. 获取订单趋势数据

**接口描述**: 获取订单趋势统计数据

- **URL**: `GET /api/adoption-orders/statistics/trend`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| farmId | Long | 否 | - | 农场ID |
| period | String | 否 | 7d | 统计周期：7d, 30d, 90d, 1y |
| granularity | String | 否 | day | 粒度：hour, day, week, month |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "period": "7d",
    "granularity": "day",
    "data": [
      {
        "date": "2025-01-13",
        "orderCount": 5,
        "revenue": 880.00,
        "paidCount": 4,
        "paidRevenue": 704.00
      },
      {
        "date": "2025-01-14",
        "orderCount": 8,
        "revenue": 1408.00,
        "paidCount": 7,
        "paidRevenue": 1232.00
      }
    ]
  }
}
```

### 3. 获取热门项目排行

**接口描述**: 获取热门认养项目排行

- **URL**: `GET /api/adoption-orders/statistics/popular-projects`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| farmId | Long | 否 | - | 农场ID |
| limit | Integer | 否 | 10 | 限制数量 |
| startDate | String | 否 | - | 开始日期 |
| endDate | String | 否 | - | 结束日期 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "projectId": 1,
      "projectName": "有机小白菜认养项目",
      "farmName": "绿野农场",
      "cropName": "小白菜",
      "orderCount": 25,
      "unitCount": 45,
      "revenue": 3960.00,
      "averageOrderAmount": 158.40,
      "adoptionRate": 45.0
    }
  ]
}
```

### 4. 获取用户消费排行

**接口描述**: 获取用户消费排行

- **URL**: `GET /api/adoption-orders/statistics/top-users`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| farmId | Long | 否 | - | 农场ID |
| limit | Integer | 否 | 10 | 限制数量 |
| startDate | String | 否 | - | 开始日期 |
| endDate | String | 否 | - | 结束日期 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "userId": 1,
      "userName": "用户A",
      "userPhone": "138****8000",
      "orderCount": 8,
      "totalAmount": 1408.00,
      "averageOrderAmount": 176.00,
      "lastOrderTime": "2025-01-19T16:00:00"
    }
  ]
}
```

## 导出功能

### 1. 导出订单数据

**接口描述**: 导出订单数据到Excel

- **URL**: `GET /api/adoption-orders/export`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**: 同订单列表筛选参数
- **响应**: Excel文件下载

### 2. 导出统计报表

**接口描述**: 导出统计报表

- **URL**: `GET /api/adoption-orders/statistics/export`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| reportType | String | 是 | 报表类型：overview, trend, projects, users |
| farmId | Long | 否 | 农场ID |
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |

- **响应**: Excel文件下载

## 数据字典

### 订单状态 (orderStatus)

| 值 | 说明 | 可操作 |
|----|------|--------|
| 1 | 待支付 | 可取消、可支付 |
| 2 | 已支付 | 可退款、可完成 |
| 3 | 已完成 | 只读状态 |
| 4 | 已取消 | 只读状态 |
| 5 | 已退款 | 只读状态 |

### 支付方式 (paymentMethod)

| 值 | 说明 |
|----|------|
| wechat | 微信支付 |
| alipay | 支付宝 |

### 统计周期 (period)

| 值 | 说明 |
|----|------|
| 7d | 最近7天 |
| 30d | 最近30天 |
| 90d | 最近90天 |
| 1y | 最近1年 |

## 业务规则

1. **订单状态流转**：
   - 待支付 → 已支付：支付成功
   - 待支付 → 已取消：超时或主动取消
   - 已支付 → 已完成：项目完成
   - 已支付 → 已退款：退款处理

2. **退款规则**：
   - 只有已支付状态的订单可以退款
   - 退款金额不能超过实付金额
   - 退款后会释放认养单元

3. **权限规则**：
   - 管理员可以查看和操作所有订单
   - 农场主只能查看自己农场相关的订单

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未认证或Token无效 |
| 403 | 权限不足 |
| 404 | 订单不存在 |
| 409 | 业务冲突（如状态不允许操作） |
| 500 | 服务器内部错误 |
