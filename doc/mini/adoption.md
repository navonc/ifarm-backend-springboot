# 认养模块 API 文档

## 概述

认养模块是iFarm系统的核心功能，提供认养项目浏览、下单、支付、认养记录管理等功能。用户可以认养农场中的种植单元，参与农作物的种植过程。

## 认养项目管理

**基础路径**: `/adoption-projects`

### 1. 获取认养项目列表

**接口描述**: 获取认养项目列表，支持分页和筛选

- **URL**: `GET /adoption-projects`
- **权限**: 无需认证
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页大小 |
| cropId | Long | 否 | - | 作物ID |
| projectStatus | Integer | 否 | - | 项目状态 |
| name | String | 否 | - | 项目名称（模糊搜索） |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "有机小白菜认养项目",
        "description": "采用有机种植方式，无农药无化肥，健康安全",
        "plotId": 1,
        "plotName": "A区地块",
        "farmId": 1,
        "farmName": "绿野农场",
        "cropId": 1,
        "cropName": "小白菜",
        "totalUnits": 100,
        "availableUnits": 65,
        "unitArea": 2.0,
        "unitPrice": 88.00,
        "expectedYield": 2.5,
        "plantingDate": "2025-02-01",
        "expectedHarvestDate": "2025-03-03",
        "projectStatus": 2,
        "projectStatusName": "认养中",
        "coverImage": "https://example.com/projects/project1.jpg",
        "images": ["https://example.com/projects/project1_1.jpg"],
        "adoptionRate": 35.0,
        "createTime": "2025-01-19T10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 2. 获取认养项目详情

**接口描述**: 根据ID获取认养项目详情

- **URL**: `GET /adoption-projects/{id}`
- **权限**: 无需认证
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 项目ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "有机小白菜认养项目",
    "description": "采用有机种植方式，无农药无化肥，健康安全。从播种到收获全程透明，用户可实时查看生长过程。",
    "plotId": 1,
    "plotName": "A区地块",
    "farmId": 1,
    "farmName": "绿野农场",
    "farmAddress": "浙江省杭州市余杭区良渚街道农场路123号",
    "cropId": 1,
    "cropName": "小白菜",
    "cropVariety": "上海青",
    "totalUnits": 100,
    "availableUnits": 65,
    "unitArea": 2.0,
    "unitPrice": 88.00,
    "expectedYield": 2.5,
    "plantingDate": "2025-02-01",
    "expectedHarvestDate": "2025-03-03",
    "projectStatus": 2,
    "projectStatusName": "认养中",
    "coverImage": "https://example.com/projects/project1.jpg",
    "images": [
      "https://example.com/projects/project1_1.jpg",
      "https://example.com/projects/project1_2.jpg"
    ],
    "plantingPlan": "第1-7天：播种期，保持土壤湿润；第8-20天：幼苗期，适量浇水施肥；第21-30天：成长期，加强管理。",
    "careInstructions": "定期浇水，保持土壤湿润但不积水；适时施有机肥；及时除草；注意病虫害防治。",
    "harvestInstructions": "叶片饱满、颜色翠绿时即可收获；清晨或傍晚收获品质最佳；收获后及时清洗包装。",
    "adoptionRate": 35.0,
    "adoptionCount": 35,
    "createTime": "2025-01-19T10:00:00",
    "updateTime": "2025-01-19T15:30:00"
  }
}
```

### 3. 搜索认养项目

**接口描述**: 根据关键词搜索认养项目

- **URL**: `GET /adoption-projects/search`
- **权限**: 无需认证
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| limit | Integer | 否 | 20 | 限制数量 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "有机小白菜认养项目",
      "farmName": "绿野农场",
      "cropName": "小白菜",
      "unitPrice": 88.00,
      "availableUnits": 65,
      "expectedYield": 2.5,
      "plantingDate": "2025-02-01",
      "expectedHarvestDate": "2025-03-03",
      "coverImage": "https://example.com/projects/project1.jpg",
      "adoptionRate": 35.0
    }
  ]
}
```

### 4. 获取热门项目列表

**接口描述**: 获取热门认养项目列表

- **URL**: `GET /adoption-projects/popular`
- **权限**: 无需认证
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| limit | Integer | 否 | 10 | 限制数量 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "有机小白菜认养项目",
      "farmName": "绿野农场",
      "cropName": "小白菜",
      "unitPrice": 88.00,
      "availableUnits": 65,
      "coverImage": "https://example.com/projects/project1.jpg",
      "adoptionCount": 35,
      "adoptionRate": 35.0
    }
  ]
}
```

### 5. 获取项目单元列表

**接口描述**: 获取指定认养项目的所有单元信息

- **URL**: `GET /adoption-projects/{id}/units`
- **权限**: 无需认证
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 项目ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| unitStatus | Integer | 否 | - | 单元状态筛选 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "unitNumber": "A001",
      "unitStatus": 2,
      "unitStatusName": "已认养",
      "adoptionUserId": 10,
      "adoptionUserName": "用户A",
      "adoptionDate": "2025-01-19T16:05:00",
      "locationInfo": {
        "coordinates": [30.123456, 120.123456],
        "description": "地块东北角第1排第1个单元"
      },
      "createTime": "2025-01-19T10:00:00"
    }
  ]
}
```

## 认养订单管理

**基础路径**: `/api/adoption-orders` (⚠️ 注意：此模块接口尚未实现)

### 1. 创建认养订单

**接口描述**: 创建认养订单

- **URL**: `POST /api/adoption-orders`
- **权限**: 需要认证
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

```json
{
  "projectId": 1,           // 项目ID (必填)
  "unitCount": 2,           // 认养单元数量 (必填)
  "remark": "希望收获新鲜蔬菜"  // 订单备注 (可选)
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "orderNo": "AD20250119123456",
    "userId": 1,
    "projectId": 1,
    "projectName": "有机小白菜认养项目",
    "unitCount": 2,
    "unitPrice": 88.00,
    "totalAmount": 176.00,
    "discountAmount": 0.00,
    "actualAmount": 176.00,
    "orderStatus": 1,
    "orderStatusName": "待支付",
    "remark": "希望收获新鲜蔬菜",
    "createTime": "2025-01-19T16:00:00"
  }
}
```

### 2. 获取用户订单列表

**接口描述**: 获取当前用户的认养订单列表

- **URL**: `GET /api/adoption-orders/my`
- **权限**: 需要认证
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页大小 |
| orderStatus | Integer | 否 | - | 订单状态 |

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
        "projectId": 1,
        "projectName": "有机小白菜认养项目",
        "farmName": "绿野农场",
        "cropName": "小白菜",
        "unitCount": 2,
        "unitPrice": 88.00,
        "totalAmount": 176.00,
        "actualAmount": 176.00,
        "orderStatus": 2,
        "orderStatusName": "已支付",
        "paymentMethod": "wechat",
        "paymentTime": "2025-01-19T16:05:00",
        "createTime": "2025-01-19T16:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 3. 获取订单详情

**接口描述**: 根据ID获取订单详情

- **URL**: `GET /api/adoption-orders/{id}`
- **权限**: 需要认证
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
    "projectId": 1,
    "projectName": "有机小白菜认养项目",
    "farmName": "绿野农场",
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
}
```

### 4. 取消订单

**接口描述**: 取消未支付的订单

- **URL**: `PUT /api/adoption-orders/{id}/cancel`
- **权限**: 需要认证
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
  "data": null
}
```

### 5. 支付订单

**接口描述**: 支付订单（模拟支付接口）

- **URL**: `PUT /api/adoption-orders/{id}/pay`
- **权限**: 需要认证
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 订单ID |

- **请求参数**:

```json
{
  "paymentMethod": "wechat",      // 支付方式：wechat, alipay (必填)
  "paymentNo": "wx_pay_123456789" // 支付流水号 (必填)
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "支付成功",
  "data": {
    "orderId": 1,
    "orderNo": "AD20250119123456",
    "paymentTime": "2025-01-19T16:05:00",
    "actualAmount": 176.00
  }
}
```

## 数据字典

### 项目状态 (projectStatus)

| 值 | 说明 |
|----|------|
| 1 | 筹备中 |
| 2 | 认养中 |
| 3 | 种植中 |
| 4 | 收获中 |
| 5 | 已完成 |
| 6 | 已取消 |

### 订单状态 (orderStatus)

| 值 | 说明 |
|----|------|
| 1 | 待支付 |
| 2 | 已支付 |
| 3 | 已完成 |
| 4 | 已取消 |
| 5 | 已退款 |

### 支付方式 (paymentMethod)

| 值 | 说明 |
|----|------|
| wechat | 微信支付 |
| alipay | 支付宝 |

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未认证或Token无效 |
| 403 | 权限不足 |
| 404 | 项目或订单不存在 |
| 409 | 项目不可认养或单元数量不足 |
| 500 | 服务器内部错误 |

## 认养记录管理

**基础路径**: `/api/adoption-records` (⚠️ 注意：此模块接口尚未实现)

### 1. 获取用户认养记录列表

**接口描述**: 获取当前用户的认养记录列表

- **URL**: `GET /api/adoption-records/my`
- **权限**: 需要认证
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页大小 |
| adoptionStatus | Integer | 否 | - | 认养状态 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "orderId": 1,
        "orderNo": "AD20250119123456",
        "projectId": 1,
        "projectName": "有机小白菜认养项目",
        "farmName": "绿野农场",
        "cropName": "小白菜",
        "unitId": 1,
        "unitNumber": "A001",
        "adoptionStatus": 2,
        "adoptionStatusName": "种植中",
        "adoptionDate": "2025-01-19T16:05:00",
        "plantingDate": "2025-02-01T08:00:00",
        "expectedHarvestDate": "2025-03-03",
        "actualYield": null,
        "qualityGrade": null,
        "createTime": "2025-01-19T16:05:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 2. 获取认养记录详情

**接口描述**: 根据ID获取认养记录详情

- **URL**: `GET /api/adoption-records/{id}`
- **权限**: 需要认证
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 认养记录ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "orderId": 1,
    "orderNo": "AD20250119123456",
    "projectId": 1,
    "projectName": "有机小白菜认养项目",
    "farmName": "绿野农场",
    "farmAddress": "浙江省杭州市余杭区良渚街道农场路123号",
    "cropName": "小白菜",
    "cropVariety": "上海青",
    "unitId": 1,
    "unitNumber": "A001",
    "unitArea": 2.0,
    "expectedYield": 2.5,
    "adoptionStatus": 2,
    "adoptionStatusName": "种植中",
    "adoptionDate": "2025-01-19T16:05:00",
    "plantingDate": "2025-02-01T08:00:00",
    "expectedHarvestDate": "2025-03-03",
    "harvestDate": null,
    "actualYield": null,
    "qualityGrade": null,
    "createTime": "2025-01-19T16:05:00",
    "updateTime": "2025-02-01T08:00:00"
  }
}
```

### 认养状态 (adoptionStatus)

| 值 | 说明 |
|----|------|
| 1 | 已认养 |
| 2 | 种植中 |
| 3 | 待收获 |
| 4 | 已收获 |
| 5 | 已完成 |

## 注意事项

1. 订单创建后有30分钟的支付时间，超时将自动取消
2. 只有状态为"认养中"的项目才能创建订单
3. 订单支付成功后会自动创建认养记录
4. 取消订单会释放预占用的项目单元
5. 认养记录创建后，用户可以查看对应单元的生长记录
