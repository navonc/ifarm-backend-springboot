# 生长记录管理模块 API 文档

## 概述

生长记录管理模块为农场主提供作物生长过程的记录和管理功能，包括每日生长记录、环境数据记录、图片上传、收获记录等。管理员可以查看所有记录。

## 生长记录管理

**基础路径**: `/api/growth-records` (⚠️ 注意：此模块接口尚未实现)

### 1. 获取生长记录列表（管理端）

**接口描述**: 获取生长记录列表，支持分页和筛选

- **URL**: `GET /api/growth-records/admin`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页大小 |
| projectId | Long | 否 | - | 项目ID |
| farmId | Long | 否 | - | 农场ID |
| growthStage | String | 否 | - | 生长阶段 |
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
        "farmId": 1,
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
        "description": "今日完成播种工作，种子均匀撒播，覆土适量。",
        "imageCount": 3,
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

### 2. 获取我的生长记录列表

**接口描述**: 获取当前农场主的生长记录列表

- **URL**: `GET /api/growth-records/my`
- **权限**: 需要认证（农场主）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**: 同管理端列表参数
- **响应示例**: 同管理端列表响应

### 3. 创建生长记录

**接口描述**: 创建新的生长记录

- **URL**: `POST /api/growth-records`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

```json
{
  "projectId": 1,                        // 项目ID (必填)
  "recordDate": "2025-02-01",            // 记录日期 (必填)
  "growthStage": "播种期",                // 生长阶段 (可选)
  "growthStatus": "正常",                 // 生长状态 (可选)
  "height": 0.0,                         // 植株高度（cm） (可选)
  "weather": "晴",                       // 天气情况 (可选)
  "temperatureHigh": 18,                 // 最高温度（℃） (可选)
  "temperatureLow": 8,                   // 最低温度（℃） (可选)
  "humidity": 65,                        // 湿度（%） (可选)
  "soilMoisture": 70,                    // 土壤湿度（%） (可选)
  "lightIntensity": 8500,                // 光照强度（lux） (可选)
  "phValue": 6.8,                        // pH值 (可选)
  "description": "今日完成播种工作，种子均匀撒播，覆土适量。",  // 记录描述 (可选)
  "careActions": "浇水、覆土、标记",       // 养护操作 (可选)
  "nextActions": "保持土壤湿润，观察发芽情况",  // 下步计划 (可选)
  "images": [                            // 图片列表 (可选)
    "https://example.com/growth/20250201_1.jpg",
    "https://example.com/growth/20250201_2.jpg"
  ]
}
```

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
    "description": "今日完成播种工作，种子均匀撒播，覆土适量。",
    "careActions": "浇水、覆土、标记",
    "nextActions": "保持土壤湿润，观察发芽情况",
    "images": [
      "https://example.com/growth/20250201_1.jpg",
      "https://example.com/growth/20250201_2.jpg"
    ],
    "recordBy": "张农场主",
    "createTime": "2025-02-01T18:00:00"
  }
}
```

### 4. 更新生长记录

**接口描述**: 更新生长记录信息

- **URL**: `PUT /api/growth-records/{id}`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 生长记录ID |

- **请求参数**: 同创建记录，所有字段都是可选的
- **响应示例**: 同创建记录响应

### 5. 删除生长记录

**接口描述**: 删除生长记录

- **URL**: `DELETE /api/growth-records/{id}`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 生长记录ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 6. 批量创建生长记录

**接口描述**: 批量创建生长记录（用于补录）

- **URL**: `POST /api/growth-records/batch`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

```json
{
  "projectId": 1,                        // 项目ID (必填)
  "records": [                           // 记录列表 (必填)
    {
      "recordDate": "2025-02-01",
      "growthStage": "播种期",
      "description": "播种记录"
    },
    {
      "recordDate": "2025-02-02",
      "growthStage": "发芽期",
      "description": "发芽记录"
    }
  ]
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "批量创建成功",
  "data": {
    "successCount": 2,
    "failureCount": 0,
    "createdIds": [1, 2]
  }
}
```

## 收获记录管理

**基础路径**: `/api/harvest-records` (⚠️ 注意：此模块接口尚未实现)

### 1. 获取收获记录列表

**接口描述**: 获取收获记录列表

- **URL**: `GET /api/harvest-records/admin`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页大小 |
| projectId | Long | 否 | - | 项目ID |
| farmId | Long | 否 | - | 农场ID |
| startDate | String | 否 | - | 开始日期 |
| endDate | String | 否 | - | 结束日期 |

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
        "farmName": "绿野农场",
        "cropName": "小白菜",
        "harvestDate": "2025-03-03",
        "totalYield": 250.5,
        "averageYield": 2.5,
        "qualityGrade": "优",
        "harvestUnits": 100,
        "weather": "晴",
        "description": "收获顺利，蔬菜品质优良，色泽翠绿，口感鲜嫩。",
        "imageCount": 5,
        "harvestBy": "张农场主",
        "createTime": "2025-03-03T16:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 2. 创建收获记录

**接口描述**: 创建收获记录

- **URL**: `POST /api/harvest-records`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

```json
{
  "projectId": 1,                        // 项目ID (必填)
  "harvestDate": "2025-03-03",           // 收获日期 (必填)
  "totalYield": 250.5,                   // 总产量（kg） (可选)
  "averageYield": 2.5,                   // 平均产量（kg/单元） (可选)
  "qualityGrade": "优",                   // 品质等级 (可选)
  "harvestUnits": 100,                   // 收获单元数 (可选)
  "weather": "晴",                       // 天气情况 (可选)
  "description": "收获顺利，蔬菜品质优良，色泽翠绿，口感鲜嫩。",  // 收获描述 (可选)
  "harvestMethod": "人工采摘",            // 收获方式 (可选)
  "storageMethod": "冷藏保存",            // 储存方式 (可选)
  "packagingMethod": "真空包装",          // 包装方式 (可选)
  "images": [                            // 图片列表 (可选)
    "https://example.com/harvest/20250303_1.jpg",
    "https://example.com/harvest/20250303_2.jpg"
  ],
  "unitDetails": [                       // 单元详情 (可选)
    {
      "unitId": 1,
      "unitNumber": "A001",
      "yield": 2.8,
      "qualityGrade": "优"
    }
  ]
}
```

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
    "harvestDate": "2025-03-03",
    "totalYield": 250.5,
    "averageYield": 2.5,
    "qualityGrade": "优",
    "harvestUnits": 100,
    "weather": "晴",
    "description": "收获顺利，蔬菜品质优良，色泽翠绿，口感鲜嫩。",
    "harvestMethod": "人工采摘",
    "storageMethod": "冷藏保存",
    "packagingMethod": "真空包装",
    "images": [
      "https://example.com/harvest/20250303_1.jpg",
      "https://example.com/harvest/20250303_2.jpg"
    ],
    "harvestBy": "张农场主",
    "createTime": "2025-03-03T16:00:00"
  }
}
```

### 3. 更新收获记录

**接口描述**: 更新收获记录信息

- **URL**: `PUT /api/harvest-records/{id}`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 收获记录ID |

- **请求参数**: 同创建记录，所有字段都是可选的
- **响应示例**: 同创建记录响应

## 媒体文件管理

**基础路径**: `/api/media-files` (⚠️ 注意：此模块接口尚未实现)

### 1. 上传图片

**接口描述**: 上传生长记录或收获记录的图片

- **URL**: `POST /api/media-files/upload`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**: 文件上传（multipart/form-data）

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | File | 是 | 图片文件 |
| type | String | 是 | 文件类型：growth, harvest, farm, crop |
| relatedId | Long | 否 | 关联ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "id": 1,
    "fileName": "growth_20250201_001.jpg",
    "originalName": "IMG_001.jpg",
    "fileUrl": "https://example.com/growth/20250201_1.jpg",
    "fileSize": 1024000,
    "fileType": "image/jpeg",
    "type": "growth",
    "relatedId": 1,
    "createTime": "2025-02-01T18:00:00"
  }
}
```

### 2. 批量上传图片

**接口描述**: 批量上传图片

- **URL**: `POST /api/media-files/batch-upload`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**: 多文件上传

- **响应示例**:

```json
{
  "code": 200,
  "message": "批量上传成功",
  "data": {
    "successCount": 3,
    "failureCount": 1,
    "successFiles": [
      {
        "id": 1,
        "fileName": "growth_20250201_001.jpg",
        "fileUrl": "https://example.com/growth/20250201_1.jpg"
      }
    ],
    "failureFiles": [
      {
        "fileName": "invalid.txt",
        "reason": "文件格式不支持"
      }
    ]
  }
}
```

### 3. 删除媒体文件

**接口描述**: 删除媒体文件

- **URL**: `DELETE /api/media-files/{id}`
- **权限**: 需要认证（农场主/管理员）
- **请求头**: `Authorization: Bearer {accessToken}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 媒体文件ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
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

### 文件类型 (type)

- growth：生长记录图片
- harvest：收获记录图片
- farm：农场图片
- crop：作物图片

## 业务规则

1. **记录创建规则**：
   - 每个项目每天只能创建一条生长记录
   - 记录日期不能超过当前日期
   - 只能为自己的项目创建记录

2. **收获记录规则**：
   - 项目状态必须为"收获中"或"已完成"
   - 收获日期不能早于种植日期
   - 总产量不能为负数

3. **图片上传规则**：
   - 支持JPG、PNG、WebP格式
   - 单个文件大小不超过5MB
   - 单次最多上传10个文件

## 权限说明

- **管理员**: 可以查看和管理所有生长记录
- **农场主**: 只能管理自己农场项目的生长记录
- **普通用户**: 无权限访问管理接口

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未认证或Token无效 |
| 403 | 权限不足 |
| 404 | 记录不存在 |
| 409 | 业务冲突（如重复记录） |
| 413 | 文件过大 |
| 415 | 文件格式不支持 |
| 500 | 服务器内部错误 |
