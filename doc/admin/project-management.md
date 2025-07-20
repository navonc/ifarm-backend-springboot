# 认养项目管理模块 API 文档

## 概述

认养项目管理模块为农场主和管理员提供认养项目的创建、管理、监控等功能。农场主可以在自己的地块上创建认养项目，管理员可以监控所有项目。

## 认养项目管理

**基础路径**: `/adoption-projects`

### 1. 获取认养项目列表

**接口描述**: 获取认养项目列表，支持分页和筛选

- **URL**: `GET /adoption-projects`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页大小 |
| name | String | 否 | - | 项目名称（模糊搜索） |
| farmId | Long | 否 | - | 农场ID |
| plotId | Long | 否 | - | 地块ID |
| cropId | Long | 否 | - | 作物ID |
| projectStatus | Integer | 否 | - | 项目状态 |
| startDate | String | 否 | - | 开始日期 (YYYY-MM-DD) |
| endDate | String | 否 | - | 结束日期 (YYYY-MM-DD) |

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
        "farmOwner": "张农场主",
        "cropId": 1,
        "cropName": "小白菜",
        "totalUnits": 100,
        "availableUnits": 65,
        "adoptedUnits": 35,
        "unitArea": 2.0,
        "unitPrice": 88.00,
        "expectedYield": 2.5,
        "plantingDate": "2025-02-01",
        "expectedHarvestDate": "2025-03-03",
        "projectStatus": 2,
        "projectStatusName": "认养中",
        "coverImage": "https://example.com/projects/project1.jpg",
        "adoptionRate": 35.0,
        "totalRevenue": 3080.00,
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

### 2. 获取认养项目详情

**接口描述**: 根据ID获取认养项目详情

- **URL**: `GET /adoption-projects/{id}`
- **权限**: 需要认证（农场主）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页大小 |
| projectStatus | Integer | 否 | - | 项目状态 |

- **响应示例**: 同管理端项目列表

### 3. 创建认养项目

**接口描述**: 创建新的认养项目

- **URL**: `POST /adoption-projects`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

```json
{
  "name": "有机小白菜认养项目",              // 项目名称 (必填)
  "description": "采用有机种植方式，无农药无化肥，健康安全",  // 项目描述 (可选)
  "plotId": 1,                            // 地块ID (必填)
  "cropId": 1,                            // 作物ID (必填)
  "totalUnits": 100,                      // 总单元数 (必填)
  "unitArea": 2.0,                        // 单元面积（平方米） (必填)
  "unitPrice": 88.00,                     // 单元认养价格 (必填)
  "expectedYield": 2.5,                   // 预期单元产量（kg） (可选)
  "plantingDate": "2025-02-01",           // 种植日期 (可选)
  "expectedHarvestDate": "2025-03-03",    // 预期收获日期 (可选)
  "coverImage": "https://example.com/projects/project1.jpg",  // 项目封面图 (可选)
  "images": ["https://example.com/projects/project1_1.jpg"],  // 项目图片 (可选)
  "plantingPlan": "第1-7天：播种期，保持土壤湿润；第8-20天：幼苗期，适量浇水施肥；第21-30天：成长期，加强管理。",  // 种植计划 (可选)
  "careInstructions": "定期浇水，保持土壤湿润但不积水；适时施有机肥；及时除草；注意病虫害防治。",  // 养护说明 (可选)
  "harvestInstructions": "叶片饱满、颜色翠绿时即可收获；清晨或傍晚收获品质最佳；收获后及时清洗包装。"  // 收获说明 (可选)
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
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
    "availableUnits": 100,
    "unitArea": 2.0,
    "unitPrice": 88.00,
    "expectedYield": 2.5,
    "plantingDate": "2025-02-01",
    "expectedHarvestDate": "2025-03-03",
    "projectStatus": 1,
    "projectStatusName": "筹备中",
    "coverImage": "https://example.com/projects/project1.jpg",
    "images": ["https://example.com/projects/project1_1.jpg"],
    "plantingPlan": "第1-7天：播种期，保持土壤湿润；第8-20天：幼苗期，适量浇水施肥；第21-30天：成长期，加强管理。",
    "careInstructions": "定期浇水，保持土壤湿润但不积水；适时施有机肥；及时除草；注意病虫害防治。",
    "harvestInstructions": "叶片饱满、颜色翠绿时即可收获；清晨或傍晚收获品质最佳；收获后及时清洗包装。",
    "createTime": "2025-01-19T10:00:00"
  }
}
```

### 4. 更新认养项目

**接口描述**: 更新认养项目信息

- **URL**: `PUT /adoption-projects/{id}`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 项目ID |

- **请求参数**: 同创建项目，所有字段都是可选的
- **响应示例**: 同创建项目响应

### 5. 删除认养项目

**接口描述**: 删除认养项目

- **URL**: `DELETE /adoption-projects/{id}`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 项目ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 6. 获取热门认养项目

**接口描述**: 获取热门认养项目列表，按认养率排序

- **URL**: `GET /adoption-projects/popular`
- **权限**: 无需认证
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| limit | Integer | 否 | 10 | 返回数量 |

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
      "adoptionRate": 35.0,
      "coverImage": "https://example.com/projects/project1.jpg"
    }
  ]
}
```

### 7. 搜索认养项目

**接口描述**: 根据关键词搜索认养项目

- **URL**: `GET /adoption-projects/search`
- **权限**: 无需认证
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页大小 |
| keyword | String | 否 | - | 搜索关键词 |

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
        "farmName": "绿野农场",
        "cropName": "小白菜",
        "unitPrice": 88.00,
        "availableUnits": 65,
        "coverImage": "https://example.com/projects/project1.jpg"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 8. 获取项目统计信息 (⚠️ 未实现)

**接口描述**: 获取项目的统计信息

- **URL**: `GET /api/adoption-projects/{id}/statistics`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
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
    "projectId": 1,
    "projectName": "有机小白菜认养项目",
    "totalUnits": 100,
    "availableUnits": 65,
    "adoptedUnits": 35,
    "adoptionRate": 35.0,
    "totalRevenue": 3080.00,
    "averageOrderAmount": 176.00,
    "orderCount": 18,
    "userCount": 15,
    "dailyAdoptions": [
      {
        "date": "2025-01-19",
        "adoptionCount": 5,
        "revenue": 440.00
      }
    ],
    "topUsers": [
      {
        "userId": 1,
        "username": "用户A",
        "adoptionCount": 3,
        "totalAmount": 528.00
      }
    ]
  }
}
```

### 8. 批量操作项目

**接口描述**: 批量操作项目（状态更新、删除等）

- **URL**: `POST /api/adoption-projects/batch`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

```json
{
  "action": "updateStatus",     // 操作类型：updateStatus, delete
  "projectIds": [1, 2, 3],      // 项目ID数组 (必填)
  "params": {                   // 操作参数
    "projectStatus": 2          // 新状态（updateStatus时需要）
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
        "projectId": 3,
        "reason": "项目状态不允许修改"
      }
    ]
  }
}
```

## 项目单元管理

**基础路径**: `/adoption-projects`

### 1. 获取项目单元列表

**接口描述**: 获取指定项目的单元列表

- **URL**: `GET /adoption-projects/{projectId}/units`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| projectId | Long | 是 | 项目ID |

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

### 2. 更新单元状态 (⚠️ 未实现)

**接口描述**: 更新项目单元状态

- **URL**: `PUT /api/project-units/{id}/status`
- **状态**: 🚧 待实现
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 单元ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| unitStatus | Integer | 是 | 单元状态：1-可认养，2-已认养，3-种植中，4-待收获，5-已收获 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

## 数据字典

### 项目状态 (projectStatus)

| 值 | 说明 | 可操作 |
|----|------|--------|
| 1 | 筹备中 | 可编辑、可删除、可启动认养 |
| 2 | 认养中 | 可暂停认养、可开始种植 |
| 3 | 种植中 | 可开始收获 |
| 4 | 收获中 | 可完成项目 |
| 5 | 已完成 | 只读状态 |
| 6 | 已取消 | 只读状态 |

### 单元状态 (unitStatus)

| 值 | 说明 |
|----|------|
| 1 | 可认养 |
| 2 | 已认养 |
| 3 | 种植中 |
| 4 | 待收获 |
| 5 | 已收获 |

## 业务规则

1. **项目创建规则**：
   - 地块状态必须为"可用"
   - 项目名称不能重复
   - 单元数量不能超过地块容量

2. **状态流转规则**：
   - 筹备中 → 认养中：需要设置认养价格
   - 认养中 → 种植中：需要有认养订单
   - 种植中 → 收获中：需要到达预期收获时间
   - 收获中 → 已完成：需要完成所有收获记录

3. **删除规则**：
   - 有认养订单的项目不能删除
   - 只能删除"筹备中"状态的项目

## 权限说明

- **管理员**: 可以管理所有认养项目
- **农场主**: 只能管理自己农场的认养项目
- **普通用户**: 无权限访问管理接口

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未认证或Token无效 |
| 403 | 权限不足 |
| 404 | 项目或单元不存在 |
| 409 | 业务冲突（如状态不允许操作） |
| 500 | 服务器内部错误 |
