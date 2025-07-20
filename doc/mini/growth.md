# 生长记录模块 API 文档

## 概述

生长记录模块提供作物生长过程的查看功能，用户可以实时了解自己认养的作物的生长状态、环境数据、图片记录等信息。

## 生长记录管理

**基础路径**: `/api/growth-records` (⚠️ 注意：此模块接口尚未实现)

### 1. 获取项目生长记录列表

**接口描述**: 获取指定项目的生长记录列表

- **URL**: `GET /api/growth-records/project/{projectId}`
- **权限**: 无需认证
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| projectId | Long | 是 | 项目ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页大小 |
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
        "projectId": 1,
        "projectName": "有机小白菜认养项目",
        "recordDate": "2025-02-01",
        "growthStage": "播种期",
        "growthStatus": "正常",
        "height": 0.0,
        "weather": "晴",
        "temperatureHigh": 18,
        "temperatureLow": 8,
        "humidity": 65,
        "soilMoisture": 70,
        "description": "今日完成播种工作，种子均匀撒播，覆土适量。",
        "images": [
          "https://example.com/growth/20250201_1.jpg",
          "https://example.com/growth/20250201_2.jpg"
        ],
        "recordBy": "张农场主",
        "createTime": "2025-02-01T18:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 2. 获取生长记录详情

**接口描述**: 根据ID获取生长记录详情

- **URL**: `GET /api/growth-records/{id}`
- **权限**: 无需认证
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 生长记录ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "projectId": 1,
    "projectName": "有机小白菜认养项目",
    "farmName": "绿野农场",
    "cropName": "小白菜",
    "recordDate": "2025-02-01",
    "growthStage": "播种期",
    "growthStatus": "正常",
    "height": 0.0,
    "weather": "晴",
    "temperatureHigh": 18,
    "temperatureLow": 8,
    "humidity": 65,
    "soilMoisture": 70,
    "lightIntensity": 8500,
    "phValue": 6.8,
    "description": "今日完成播种工作，种子均匀撒播，覆土适量。土壤湿度适宜，天气条件良好，有利于种子发芽。",
    "careActions": "浇水、覆土、标记",
    "nextActions": "保持土壤湿润，观察发芽情况",
    "images": [
      "https://example.com/growth/20250201_1.jpg",
      "https://example.com/growth/20250201_2.jpg",
      "https://example.com/growth/20250201_3.jpg"
    ],
    "recordBy": "张农场主",
    "createTime": "2025-02-01T18:00:00",
    "updateTime": "2025-02-01T18:00:00"
  }
}
```

### 3. 获取用户认养记录的生长记录

**接口描述**: 获取用户认养记录对应的生长记录列表

- **URL**: `GET /api/growth-records/my-adoption/{recordId}`
- **权限**: 需要认证
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| recordId | Long | 是 | 认养记录ID |

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
    "adoptionInfo": {
      "id": 1,
      "projectName": "有机小白菜认养项目",
      "farmName": "绿野农场",
      "cropName": "小白菜",
      "unitNumber": "A001",
      "adoptionDate": "2025-01-19T16:05:00",
      "plantingDate": "2025-02-01T08:00:00",
      "expectedHarvestDate": "2025-03-03"
    },
    "growthRecords": {
      "records": [
        {
          "id": 1,
          "recordDate": "2025-02-01",
          "growthStage": "播种期",
          "growthStatus": "正常",
          "height": 0.0,
          "weather": "晴",
          "temperatureHigh": 18,
          "temperatureLow": 8,
          "description": "今日完成播种工作，种子均匀撒播，覆土适量。",
          "images": ["https://example.com/growth/20250201_1.jpg"],
          "createTime": "2025-02-01T18:00:00"
        }
      ],
      "total": 1,
      "size": 10,
      "current": 1,
      "pages": 1
    }
  }
}
```

### 4. 获取生长阶段统计

**接口描述**: 获取项目的生长阶段统计信息

- **URL**: `GET /api/growth-records/project/{projectId}/stages`
- **权限**: 无需认证
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| projectId | Long | 是 | 项目ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "projectId": 1,
    "projectName": "有机小白菜认养项目",
    "currentStage": "幼苗期",
    "totalDays": 30,
    "currentDay": 8,
    "progress": 26.7,
    "stages": [
      {
        "stage": "播种期",
        "startDay": 1,
        "endDay": 3,
        "status": "completed",
        "description": "种子播种阶段"
      },
      {
        "stage": "发芽期",
        "startDay": 4,
        "endDay": 7,
        "status": "completed",
        "description": "种子发芽出土"
      },
      {
        "stage": "幼苗期",
        "startDay": 8,
        "endDay": 15,
        "status": "current",
        "description": "幼苗生长阶段"
      },
      {
        "stage": "成长期",
        "startDay": 16,
        "endDay": 25,
        "status": "pending",
        "description": "快速生长阶段"
      },
      {
        "stage": "成熟期",
        "startDay": 26,
        "endDay": 30,
        "status": "pending",
        "description": "成熟收获阶段"
      }
    ]
  }
}
```

## 收获记录管理

**基础路径**: `/api/harvest-records` (⚠️ 注意：此模块接口尚未实现)

### 1. 获取项目收获记录

**接口描述**: 获取指定项目的收获记录

- **URL**: `GET /api/harvest-records/project/{projectId}`
- **权限**: 无需认证
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| projectId | Long | 是 | 项目ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "projectId": 1,
      "projectName": "有机小白菜认养项目",
      "harvestDate": "2025-03-03",
      "totalYield": 250.5,
      "averageYield": 2.5,
      "qualityGrade": "优",
      "harvestUnits": 100,
      "weather": "晴",
      "description": "收获顺利，蔬菜品质优良，色泽翠绿，口感鲜嫩。",
      "images": [
        "https://example.com/harvest/20250303_1.jpg",
        "https://example.com/harvest/20250303_2.jpg"
      ],
      "harvestBy": "张农场主",
      "createTime": "2025-03-03T16:00:00"
    }
  ]
}
```

### 2. 获取用户认养记录的收获信息

**接口描述**: 获取用户认养记录对应的收获信息

- **URL**: `GET /api/harvest-records/my-adoption/{recordId}`
- **权限**: 需要认证
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| recordId | Long | 是 | 认养记录ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "adoptionInfo": {
      "id": 1,
      "projectName": "有机小白菜认养项目",
      "unitNumber": "A001",
      "expectedYield": 2.5
    },
    "harvestInfo": {
      "id": 1,
      "harvestDate": "2025-03-03",
      "actualYield": 2.8,
      "qualityGrade": "优",
      "description": "您认养的小白菜收获了！品质优良，超出预期产量。",
      "images": ["https://example.com/harvest/unit_A001.jpg"],
      "harvestBy": "张农场主"
    }
  }
}
```

## 数据字典

### 生长阶段 (growthStage)

- 播种期：种子播种阶段
- 发芽期：种子发芽出土
- 幼苗期：幼苗生长阶段
- 成长期：快速生长阶段
- 成熟期：成熟收获阶段

### 生长状态 (growthStatus)

- 正常：生长状态良好
- 缓慢：生长速度较慢
- 异常：出现病虫害等问题

### 品质等级 (qualityGrade)

- 优：品质优良
- 良：品质良好
- 中：品质一般
- 差：品质较差

### 阶段状态 (status)

- completed：已完成
- current：进行中
- pending：未开始

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未认证或Token无效 |
| 403 | 权限不足 |
| 404 | 记录不存在 |
| 500 | 服务器内部错误 |

## 注意事项

1. 生长记录由农场主每日更新，用户可实时查看
2. 环境数据包括温度、湿度、光照等信息
3. 图片记录展示作物的实际生长状态
4. 收获记录在项目完成后生成
5. 用户只能查看自己认养项目的详细生长记录
