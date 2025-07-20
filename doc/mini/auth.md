# 认证模块 API 文档

## 概述

认证模块提供用户登录、注册、Token管理等功能，支持微信小程序登录和传统用户名密码登录。

**基础路径**: `/auth`

## 接口列表

### 1. 微信小程序登录

**接口描述**: 使用微信授权码进行登录，如果用户不存在则自动注册

- **URL**: `POST /auth/wechat-login`
- **权限**: 无需认证
- **请求参数**:

```json
{
  "code": "string",           // 微信授权码 (必填)
  "userInfo": {               // 用户信息 (可选)
    "nickName": "string",     // 用户昵称
    "avatarUrl": "string",    // 头像地址
    "gender": 1,              // 性别：0-未知，1-男，2-女
    "country": "string",      // 国家
    "province": "string",     // 省份
    "city": "string"          // 城市
  }
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200,
    "userInfo": {
      "id": 1,
      "username": "wx_user_123",
      "nickname": "微信用户",
      "avatar": "https://example.com/avatar.jpg",
      "phone": null,
      "userType": 1,
      "gender": 1,
      "status": 1,
      "createTime": "2025-01-19T10:00:00"
    }
  }
}
```

### 2. 用户名密码登录

**接口描述**: 使用用户名/手机号和密码进行登录

- **URL**: `POST /auth/login`
- **权限**: 无需认证
- **请求参数**:

```json
{
  "username": "string",       // 用户名或手机号 (必填)
  "password": "string"        // 密码 (必填)
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200,
    "userInfo": {
      "id": 1,
      "username": "testuser",
      "nickname": "测试用户",
      "avatar": "https://example.com/avatar.jpg",
      "phone": "13800138000",
      "userType": 1,
      "gender": 1,
      "status": 1,
      "createTime": "2025-01-19T10:00:00"
    }
  }
}
```

### 3. 刷新Token

**接口描述**: 使用刷新Token获取新的访问Token

- **URL**: `POST /auth/refresh`
- **权限**: 无需认证
- **请求参数**:

```json
{
  "refreshToken": "string"    // 刷新Token (必填)
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200,
    "userInfo": {
      "id": 1,
      "username": "testuser",
      "nickname": "测试用户",
      "avatar": "https://example.com/avatar.jpg",
      "phone": "13800138000",
      "userType": 1,
      "gender": 1,
      "status": 1,
      "createTime": "2025-01-19T10:00:00"
    }
  }
}
```

### 4. 退出登录

**接口描述**: 用户退出登录

- **URL**: `POST /auth/logout`
- **权限**: 需要认证
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**: 无
- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 5. 获取当前用户信息

**接口描述**: 获取当前登录用户的详细信息

- **URL**: `GET /auth/profile`
- **权限**: 需要认证
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**: 无
- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "nickname": "测试用户",
    "avatar": "https://example.com/avatar.jpg",
    "phone": "13800138000",
    "userType": 1,
    "gender": 1,
    "status": 1,
    "lastLoginTime": "2025-01-19T10:00:00",
    "createTime": "2025-01-19T09:00:00",
    "updateTime": "2025-01-19T10:00:00"
  }
}
```

### 6. 更新用户信息

**接口描述**: 更新当前登录用户的基本信息

- **URL**: `PUT /auth/profile`
- **权限**: 需要认证
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

```json
{
  "nickname": "string",       // 用户昵称 (可选)
  "avatar": "string",         // 头像地址 (可选)
  "gender": 1                 // 性别：0-未知，1-男，2-女 (可选)
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "nickname": "新昵称",
    "avatar": "https://example.com/new-avatar.jpg",
    "phone": "13800138000",
    "userType": 1,
    "gender": 1,
    "status": 1,
    "lastLoginTime": "2025-01-19T10:00:00",
    "createTime": "2025-01-19T09:00:00",
    "updateTime": "2025-01-19T10:30:00"
  }
}
```

### 7. 修改密码

**接口描述**: 修改当前登录用户的密码

- **URL**: `PUT /auth/password`
- **权限**: 需要认证
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**:

```json
{
  "oldPassword": "string",    // 原密码 (必填)
  "newPassword": "string"     // 新密码 (必填)
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 8. 验证Token有效性

**接口描述**: 验证当前Token是否有效

- **URL**: `GET /auth/validate`
- **权限**: 需要认证
- **请求头**: `Authorization: Bearer {accessToken}`
- **请求参数**: 无
- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未认证或Token无效 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 注意事项

1. 所有需要认证的接口都需要在请求头中携带 `Authorization: Bearer {accessToken}`
2. Token有效期为2小时，过期后需要使用refreshToken刷新
3. 微信登录时，如果用户不存在会自动创建账号
4. 密码需要符合安全要求（长度、复杂度等）
