# 农场浏览模块 API 文档

## 概述

农场浏览模块提供农场信息查看、搜索、地块浏览等功能，用户可以浏览农场详情和认养项目。

**基础路径**: `/api/farms`

## 接口列表

### 1. 获取农场列表

**接口描述**: 获取农场列表，支持分页和搜索

- **URL**: `GET /api/farms`
- **权限**: 无需认证
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页大小 |
| farmName | String | 否 | - | 农场名称（模糊搜索） |
| ownerId | Long | 否 | - | 农场主ID |
| enabled | Boolean | 否 | - | 是否启用 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "farmName": "绿野农场",
        "description": "专注有机蔬菜种植的生态农场",
        "province": "浙江省",
        "city": "杭州市",
        "district": "余杭区",
        "address": "余杭区良渚街道农场路123号",
        "latitude": 30.123456,
        "longitude": 120.123456,
        "totalArea": 50.5,
        "coverImage": "https://example.com/farm-cover.jpg",
        "images": ["https://example.com/farm1.jpg", "https://example.com/farm2.jpg"],
        "contactPhone": "0571-12345678",
        "businessHours": "8:00-18:00",
        "enabled": true,
        "ownerId": 2,
        "ownerName": "张农场主",
        "plotCount": 5,
        "projectCount": 8,
        "totalAdoptionCount": 120,
        "rating": 4.8,
        "reviewCount": 45,
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

### 2. 获取农场详情

**接口描述**: 根据ID获取农场详情

- **URL**: `GET /api/farms/{id}`
- **权限**: 无需认证
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 农场ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "farmName": "绿野农场",
    "description": "专注有机蔬菜种植的生态农场，采用现代化种植技术，确保农产品的安全和品质。",
    "province": "浙江省",
    "city": "杭州市",
    "district": "余杭区",
    "address": "余杭区良渚街道农场路123号",
    "latitude": 30.123456,
    "longitude": 120.123456,
    "totalArea": 50.5,
    "coverImage": "https://example.com/farm-cover.jpg",
    "images": [
      "https://example.com/farm1.jpg",
      "https://example.com/farm2.jpg",
      "https://example.com/farm3.jpg"
    ],
    "licenseNumber": "330110123456789",
    "certification": {
      "organic": true,
      "greenFood": true,
      "certificates": ["有机认证", "绿色食品认证"]
    },
    "contactPhone": "0571-12345678",
    "businessHours": "8:00-18:00",
    "enabled": true,
    "ownerId": 2,
    "ownerName": "张农场主",
    "plotCount": 5,
    "projectCount": 8,
    "totalAdoptionCount": 120,
    "rating": 4.8,
    "reviewCount": 45,
    "createTime": "2025-01-19T10:00:00",
    "updateTime": "2025-01-19T15:30:00"
  }
}
```

### 3. 搜索农场

**接口描述**: 根据关键词搜索农场

- **URL**: `GET /api/farms/search`
- **权限**: 无需认证
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| keyword | String | 是 | - | 搜索关键词 |
| limit | Integer | 否 | 10 | 限制数量 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "farmName": "绿野农场",
      "description": "专注有机蔬菜种植的生态农场",
      "province": "浙江省",
      "city": "杭州市",
      "district": "余杭区",
      "address": "余杭区良渚街道农场路123号",
      "coverImage": "https://example.com/farm-cover.jpg",
      "rating": 4.8,
      "reviewCount": 45,
      "ownerName": "张农场主"
    }
  ]
}
```

### 4. 获取农场地块列表

**接口描述**: 获取指定农场的所有地块

- **URL**: `GET /api/farm-plots/farm/{farmId}`
- **权限**: 无需认证
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| farmId | Long | 是 | 农场ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "plotName": "A区地块",
      "description": "适合种植叶菜类蔬菜的优质地块",
      "farmId": 1,
      "farmName": "绿野农场",
      "area": 1000.0,
      "soilType": "壤土",
      "irrigationType": "滴灌",
      "locationInfo": {
        "coordinates": [30.123456, 120.123456],
        "boundaries": []
      },
      "images": ["https://example.com/plot1.jpg"],
      "plotStatus": 2,
      "plotStatusName": "种植中",
      "enabled": true,
      "projectCount": 2,
      "unitCount": 50,
      "adoptedUnitCount": 35,
      "adoptionRate": 70.0,
      "currentCrop": "小白菜",
      "createTime": "2025-01-19T10:00:00"
    }
  ]
}
```

### 5. 获取地块详情

**接口描述**: 根据ID获取地块详情

- **URL**: `GET /api/farm-plots/{id}`
- **权限**: 无需认证
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 地块ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "plotName": "A区地块",
    "description": "适合种植叶菜类蔬菜的优质地块，土壤肥沃，排水良好。",
    "farmId": 1,
    "farmName": "绿野农场",
    "area": 1000.0,
    "soilType": "壤土",
    "irrigationType": "滴灌",
    "locationInfo": {
      "coordinates": [30.123456, 120.123456],
      "boundaries": [
        [30.123456, 120.123456],
        [30.123556, 120.123456],
        [30.123556, 120.123556],
        [30.123456, 120.123556]
      ]
    },
    "images": [
      "https://example.com/plot1.jpg",
      "https://example.com/plot2.jpg"
    ],
    "plotStatus": 2,
    "plotStatusName": "种植中",
    "enabled": true,
    "projectCount": 2,
    "unitCount": 50,
    "adoptedUnitCount": 35,
    "adoptionRate": 70.0,
    "currentCrop": "小白菜",
    "createTime": "2025-01-19T10:00:00",
    "updateTime": "2025-01-19T15:30:00"
  }
}
```

## 数据字典

### 地块状态 (plotStatus)

| 值 | 说明 |
|----|------|
| 1 | 空闲 |
| 2 | 种植中 |
| 3 | 收获中 |
| 4 | 休耕 |

### 用户类型 (userType)

| 值 | 说明 |
|----|------|
| 1 | 普通用户 |
| 2 | 农场主 |
| 3 | 管理员 |

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 404 | 农场或地块不存在 |
| 500 | 服务器内部错误 |

## 注意事项

1. 农场列表默认只返回启用状态的农场
2. 地块状态会影响认养项目的可用性
3. 坐标系统使用WGS84标准
4. 图片地址为完整的URL路径
