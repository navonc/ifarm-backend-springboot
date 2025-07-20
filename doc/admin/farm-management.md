# 农场管理模块 API 文档

## 概述

农场管理模块为农场主和管理员提供农场信息管理、地块管理、认养项目管理等功能。农场主可以管理自己的农场，管理员可以管理所有农场。

## 农场管理

**基础路径**: `/api/farms`

### 1. 获取农场列表（管理端）

**接口描述**: 获取农场列表，支持分页和高级筛选

- **URL**: `GET /api/farms`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页大小 |
| farmName | String | 否 | - | 农场名称（模糊搜索） |
| ownerId | Long | 否 | - | 农场主ID |
| enabled | Boolean | 否 | - | 是否启用 |
| province | String | 否 | - | 省份 |
| city | String | 否 | - | 城市 |

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
        "totalArea": 50.5,
        "coverImage": "https://example.com/farm-cover.jpg",
        "licenseNumber": "330110123456789",
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
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 2. 获取我的农场列表

**接口描述**: 获取当前用户（农场主）的农场列表

- **URL**: `GET /api/farms/my`
- **权限**: 需要认证（农场主）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**: 无
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
      "totalArea": 50.5,
      "coverImage": "https://example.com/farm-cover.jpg",
      "enabled": true,
      "plotCount": 5,
      "projectCount": 8,
      "totalAdoptionCount": 120,
      "createTime": "2025-01-19T10:00:00"
    }
  ]
}
```

### 3. 创建农场

**接口描述**: 创建新的农场

- **URL**: `POST /api/farms`
- **权限**: 需要认证（管理员/农场主）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

```json
{
  "farmName": "绿野农场",                    // 农场名称 (必填)
  "description": "专注有机蔬菜种植的生态农场",  // 农场描述 (可选)
  "province": "浙江省",                     // 省份 (必填)
  "city": "杭州市",                        // 城市 (必填)
  "district": "余杭区",                    // 区县 (必填)
  "address": "余杭区良渚街道农场路123号",     // 详细地址 (必填)
  "latitude": 30.123456,                  // 纬度 (可选)
  "longitude": 120.123456,                // 经度 (可选)
  "totalArea": 50.5,                      // 总面积（亩） (可选)
  "coverImage": "https://example.com/farm-cover.jpg",  // 封面图片 (可选)
  "images": ["https://example.com/farm1.jpg"],         // 农场图片 (可选)
  "licenseNumber": "330110123456789",     // 营业执照号 (可选)
  "certification": {                      // 认证信息 (可选)
    "organic": true,
    "greenFood": true
  },
  "contactPhone": "0571-12345678",        // 联系电话 (可选)
  "businessHours": "8:00-18:00",          // 营业时间 (可选)
  "enabled": true                         // 是否启用 (可选，默认true)
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
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
    "images": ["https://example.com/farm1.jpg"],
    "licenseNumber": "330110123456789",
    "contactPhone": "0571-12345678",
    "businessHours": "8:00-18:00",
    "enabled": true,
    "ownerId": 2,
    "ownerName": "张农场主",
    "createTime": "2025-01-19T10:00:00"
  }
}
```

### 4. 更新农场信息

**接口描述**: 更新农场信息

- **URL**: `PUT /api/farms/{id}`
- **权限**: 需要认证（管理员/农场主）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 农场ID |

- **请求参数**: 同创建农场，所有字段都是可选的
- **响应示例**: 同创建农场响应

### 5. 删除农场

**接口描述**: 删除农场（仅管理员）

- **URL**: `DELETE /api/farms/{id}`
- **权限**: 需要认证（管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 农场ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 6. 更新农场状态

**接口描述**: 启用或禁用农场（仅管理员）

- **URL**: `PUT /api/farms/{id}/status`
- **权限**: 需要认证（管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 农场ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| enabled | Boolean | 是 | 是否启用 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

## 地块管理

**基础路径**: `/api/farm-plots`

### 1. 获取地块列表

**接口描述**: 获取地块列表，支持分页和筛选

- **URL**: `GET /api/farm-plots`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页大小 |
| plotName | String | 否 | - | 地块名称（模糊搜索） |
| farmId | Long | 否 | - | 农场ID |
| plotStatus | Integer | 否 | - | 地块状态 |
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
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 2. 创建地块

**接口描述**: 创建新的地块

- **URL**: `POST /api/farm-plots`
- **权限**: 需要认证（管理员/农场主）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

```json
{
  "farmId": 1,                           // 农场ID (必填)
  "plotName": "A区地块",                  // 地块名称 (必填)
  "description": "适合种植叶菜类蔬菜的优质地块",  // 地块描述 (可选)
  "area": 1000.0,                        // 地块面积（平方米） (必填)
  "soilType": "壤土",                     // 土壤类型 (可选)
  "irrigationType": "滴灌",               // 灌溉方式 (可选)
  "locationInfo": {                      // 位置信息 (可选)
    "coordinates": [30.123456, 120.123456],
    "boundaries": []
  },
  "images": ["https://example.com/plot1.jpg"]  // 地块图片 (可选)
}
```

- **响应示例**: 同地块列表中的单个地块数据

### 3. 更新地块信息

**接口描述**: 更新地块信息

- **URL**: `PUT /api/farm-plots/{id}`
- **权限**: 需要认证（管理员/农场主）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 地块ID |

- **请求参数**: 同创建地块，所有字段都是可选的
- **响应示例**: 同创建地块响应

### 4. 删除地块

**接口描述**: 删除地块

- **URL**: `DELETE /api/farm-plots/{id}`
- **权限**: 需要认证（管理员/农场主）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 地块ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 5. 更新地块状态

**接口描述**: 更新地块状态

- **URL**: `PUT /api/farm-plots/{id}/status`
- **权限**: 需要认证（管理员/农场主）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 地块ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| plotStatus | Integer | 是 | 地块状态：1-空闲，2-种植中，3-收获中，4-休耕 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 6. 启用/禁用地块

**接口描述**: 启用或禁用地块

- **URL**: `PUT /api/farm-plots/{id}/enable`
- **权限**: 需要认证（管理员/农场主）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 地块ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| enabled | Boolean | 是 | 是否启用 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 7. 根据状态获取地块

**接口描述**: 根据地块状态获取地块列表

- **URL**: `GET /api/farm-plots/status/{status}`
- **权限**: 无需认证
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | Integer | 是 | 地块状态：1-空闲，2-种植中，3-收获中，4-休耕 |

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
      "plotStatus": 2,
      "plotStatusName": "种植中",
      "enabled": true,
      "createTime": "2025-01-19T10:00:00"
    }
  ]
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

### 土壤类型

- 壤土：最适合大多数作物
- 沙土：排水良好，适合根茎类
- 黏土：保水性好，适合水生作物
- 腐殖土：营养丰富，适合叶菜类

### 灌溉方式

- 滴灌：节水高效
- 喷灌：覆盖面广
- 漫灌：传统方式
- 微喷：精准灌溉

## 权限说明

- **管理员**: 可以管理所有农场和地块
- **农场主**: 只能管理自己的农场和地块
- **普通用户**: 无权限访问管理接口

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未认证或Token无效 |
| 403 | 权限不足 |
| 404 | 农场或地块不存在 |
| 409 | 业务冲突（如地块正在使用中） |
| 500 | 服务器内部错误 |
