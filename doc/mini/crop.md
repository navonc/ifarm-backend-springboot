# 作物与分类模块 API 文档

## 概述

作物与分类模块提供作物信息查看、分类浏览等功能，用户可以了解不同作物的特性和种植信息。

## 分类管理

**基础路径**: `/api/categories`

### 1. 获取分类列表

**接口描述**: 获取分类列表，支持分页和搜索

- **URL**: `GET /api/categories`
- **权限**: 无需认证
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页大小 |
| categoryName | String | 否 | - | 分类名称（模糊搜索） |
| parentId | Long | 否 | - | 父分类ID |
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
        "categoryName": "叶菜类",
        "code": "leafy_vegetables",
        "icon": "https://example.com/icons/leafy.png",
        "parentId": 0,
        "parentName": null,
        "sortOrder": 1,
        "enabled": true,
        "childrenCount": 5,
        "cropCount": 12,
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

### 2. 获取分类树

**接口描述**: 获取完整的分类树结构

- **URL**: `GET /api/categories/tree`
- **权限**: 无需认证
- **请求参数**: 无
- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "categoryName": "叶菜类",
      "code": "leafy_vegetables",
      "icon": "https://example.com/icons/leafy.png",
      "parentId": 0,
      "sortOrder": 1,
      "enabled": true,
      "children": [
        {
          "id": 2,
          "categoryName": "白菜类",
          "code": "cabbage",
          "icon": "https://example.com/icons/cabbage.png",
          "parentId": 1,
          "sortOrder": 1,
          "enabled": true,
          "children": []
        }
      ]
    }
  ]
}
```

### 3. 获取分类详情

**接口描述**: 根据ID获取分类详情

- **URL**: `GET /api/categories/{id}`
- **权限**: 无需认证
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 分类ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "categoryName": "叶菜类",
    "code": "leafy_vegetables",
    "icon": "https://example.com/icons/leafy.png",
    "parentId": 0,
    "parentName": null,
    "sortOrder": 1,
    "enabled": true,
    "childrenCount": 5,
    "cropCount": 12,
    "createTime": "2025-01-19T10:00:00",
    "updateTime": "2025-01-19T15:30:00"
  }
}
```

### 4. 获取子分类

**接口描述**: 获取指定分类的子分类列表

- **URL**: `GET /api/categories/{id}/children`
- **权限**: 无需认证
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 父分类ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 2,
      "categoryName": "白菜类",
      "code": "cabbage",
      "icon": "https://example.com/icons/cabbage.png",
      "parentId": 1,
      "parentName": "叶菜类",
      "sortOrder": 1,
      "enabled": true,
      "childrenCount": 0,
      "cropCount": 3,
      "createTime": "2025-01-19T10:00:00"
    }
  ]
}
```

## 作物管理

**基础路径**: `/api/crops`

### 1. 获取作物列表

**接口描述**: 获取作物列表，支持分页和搜索

- **URL**: `GET /api/crops`
- **权限**: 无需认证
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页大小 |
| cropName | String | 否 | - | 作物名称（模糊搜索） |
| categoryId | Long | 否 | - | 分类ID |
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
        "name": "小白菜",
        "variety": "上海青",
        "description": "营养丰富的绿叶蔬菜，口感鲜嫩",
        "categoryId": 2,
        "categoryName": "白菜类",
        "growthCycle": 30,
        "plantingSeason": "春季、秋季",
        "harvestSeason": "春季、秋季",
        "yieldPerUnit": 2.5,
        "nutritionInfo": {
          "vitamin_c": "高",
          "fiber": "丰富",
          "calories": "低"
        },
        "coverImage": "https://example.com/crops/xiaobai.jpg",
        "images": ["https://example.com/crops/xiaobai1.jpg"],
        "enabled": true,
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

### 2. 获取作物详情

**接口描述**: 根据ID获取作物详情

- **URL**: `GET /api/crops/{id}`
- **权限**: 无需认证
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 作物ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "小白菜",
    "variety": "上海青",
    "description": "营养丰富的绿叶蔬菜，口感鲜嫩，适合炒食和汤煮。",
    "categoryId": 2,
    "categoryName": "白菜类",
    "growthCycle": 30,
    "plantingSeason": "春季、秋季",
    "harvestSeason": "春季、秋季",
    "yieldPerUnit": 2.5,
    "nutritionInfo": {
      "vitamin_c": "高",
      "vitamin_a": "中等",
      "fiber": "丰富",
      "calories": "低",
      "protein": "中等"
    },
    "plantingGuide": "选择疏松肥沃的土壤，保持适当湿度，定期施肥。",
    "coverImage": "https://example.com/crops/xiaobai.jpg",
    "images": [
      "https://example.com/crops/xiaobai1.jpg",
      "https://example.com/crops/xiaobai2.jpg"
    ],
    "enabled": true,
    "createTime": "2025-01-19T10:00:00",
    "updateTime": "2025-01-19T15:30:00"
  }
}
```

### 3. 根据分类获取作物

**接口描述**: 获取指定分类下的作物列表

- **URL**: `GET /api/crops/category/{categoryId}`
- **权限**: 无需认证
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryId | Long | 是 | 分类ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页大小 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "小白菜",
        "variety": "上海青",
        "description": "营养丰富的绿叶蔬菜",
        "categoryId": 2,
        "categoryName": "白菜类",
        "growthCycle": 30,
        "yieldPerUnit": 2.5,
        "coverImage": "https://example.com/crops/xiaobai.jpg",
        "enabled": true
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

## 数据字典

### 种植季节

- 春季：3-5月
- 夏季：6-8月
- 秋季：9-11月
- 冬季：12-2月

### 营养信息等级

- 高：含量丰富
- 中等：含量适中
- 低：含量较少

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 404 | 分类或作物不存在 |
| 500 | 服务器内部错误 |

## 注意事项

1. 分类支持多级结构，最多支持3级
2. 作物信息中的营养信息为JSON格式
3. 生长周期单位为天
4. 单位产量单位为公斤(kg)
