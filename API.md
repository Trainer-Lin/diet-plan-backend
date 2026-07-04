# 个人健康饮食计划系统 API 文档

## 概述

| 项目 | 说明 |
|------|------|
| 基础路径 | `http://localhost:8080` |
| 接口规范 | RESTful |
| 数据格式 | JSON |
| 统一返回 | `ApiResponse<T>` |
| 认证方式 | JWT Bearer Token（骨架阶段未完全接入） |
| API 文档 | `http://localhost:8080/swagger-ui.html` |

---

## 统一返回结构

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | Integer | 状态码，200 为成功 |
| `message` | String | 提示信息 |
| `data` | Object | 业务数据，可能为 null |

---

## 接口列表

### 1. 认证模块

基础路径：`/api/auth`

#### 1.1 用户注册

| 项目 | 内容 |
|------|------|
| 方法 | `POST` |
| 路径 | `/api/auth/register` |
| 权限 | 公开 |

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `username` | String | 是 | 用户名 |
| `email` | String | 是 | 邮箱 |
| `password` | String | 是 | 密码 |
| `nickname` | String | 是 | 昵称 |

**请求示例：**

```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "123456",
  "nickname": "Tester"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

---

#### 1.2 用户登录

| 项目 | 内容 |
|------|------|
| 方法 | `POST` |
| 路径 | `/api/auth/login` |
| 权限 | 公开 |

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `username` | String | 是 | 用户名 |
| `password` | String | 是 | 密码 |

**请求示例：**

```json
{
  "username": "testuser",
  "password": "123456"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer"
  }
}
```

---

#### 1.3 获取当前用户

| 项目 | 内容 |
|------|------|
| 方法 | `GET` |
| 路径 | `/api/auth/me` |
| 权限 | 公开（骨架阶段返回占位数据） |

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "demo",
    "email": "demo@example.com",
    "nickname": "演示用户"
  }
}
```

---

### 2. 食物库模块

基础路径：`/api/foods`

#### 2.1 获取食物列表

| 项目 | 内容 |
|------|------|
| 方法 | `GET` |
| 路径 | `/api/foods` |
| 权限 | 需认证 |

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "测试食物",
      "category": "测试",
      "servingSize": "100.00",
      "servingUnit": "克",
      "calories": 200,
      "protein": 10.00,
      "carbs": 20.00,
      "fat": 5.00
    }
  ]
}
```

---

#### 2.2 搜索食物

| 项目 | 内容 |
|------|------|
| 方法 | `GET` |
| 路径 | `/api/foods/search` |
| 权限 | 需认证 |

**查询参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `keyword` | String | 是 | 搜索关键词 |

**请求示例：**

```
GET /api/foods/search?keyword=测试
```

---

#### 2.3 创建自定义食物

| 项目 | 内容 |
|------|------|
| 方法 | `POST` |
| 路径 | `/api/foods/custom` |
| 权限 | 需认证 |

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `name` | String | 是 | 食物名称 |
| `category` | String | 是 | 食物分类 |
| `servingUnit` | String | 是 | 份量单位 |
| `servingSize` | Number | 是 | 标准份量 |
| `calories` | Integer | 是 | 热量(kcal) |
| `protein` | Number | 是 | 蛋白质(g) |
| `carbs` | Number | 是 | 碳水(g) |
| `fat` | Number | 是 | 脂肪(g) |

**请求示例：**

```json
{
  "name": "自定义食物",
  "category": "主食",
  "servingUnit": "克",
  "servingSize": 100,
  "calories": 150,
  "protein": 5.0,
  "carbs": 30.0,
  "fat": 2.0
}
```

---

### 3. 饮食记录模块

基础路径：`/api/records`

#### 3.1 查询某日饮食记录

| 项目 | 内容 |
|------|------|
| 方法 | `GET` |
| 路径 | `/api/records/daily` |
| 权限 | 需认证 |

**查询参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `date` | String | 是 | 日期，格式 `yyyy-MM-dd` |

**请求示例：**

```
GET /api/records/daily?date=2026-07-03
```

---

#### 3.2 新增饮食记录

| 项目 | 内容 |
|------|------|
| 方法 | `POST` |
| 路径 | `/api/records` |
| 权限 | 需认证 |

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `recordDate` | String | 是 | 记录日期 `yyyy-MM-dd` |
| `meal` | String | 是 | 餐次，如 breakfast/lunch/dinner |
| `note` | String | 否 | 备注 |
| `foods` | Array | 是 | 食物列表 |
| `foods[].foodId` | Long | 否 | 食物ID（可选） |
| `foods[].foodName` | String | 是 | 食物名称 |
| `foods[].amount` | String | 是 | 摄入份量 |
| `foods[].calories` | Integer | 是 | 热量 |
| `foods[].protein` | Number | 是 | 蛋白质 |
| `foods[].carbs` | Number | 是 | 碳水 |
| `foods[].fat` | Number | 是 | 脂肪 |

---

#### 3.3 删除饮食明细

| 项目 | 内容 |
|------|------|
| 方法 | `DELETE` |
| 路径 | `/api/records/item/{id}` |
| 权限 | 需认证 |

**路径参数：**

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 饮食明细ID |

---

### 4. 统计模块

基础路径：`/api/stats`

#### 4.1 今日摄入统计

| 项目 | 内容 |
|------|------|
| 方法 | `GET` |
| 路径 | `/api/stats/today` |
| 权限 | 需认证 |

---

#### 4.2 本周热量统计

| 项目 | 内容 |
|------|------|
| 方法 | `GET` |
| 路径 | `/api/stats/weekly-calories` |
| 权限 | 需认证 |

---

#### 4.3 本周宏量营养素统计

| 项目 | 内容 |
|------|------|
| 方法 | `GET` |
| 路径 | `/api/stats/weekly-macros` |
| 权限 | 需认证 |

---

#### 4.4 打卡率统计

| 项目 | 内容 |
|------|------|
| 方法 | `GET` |
| 路径 | `/api/stats/checkin` |
| 权限 | 需认证 |

---

#### 4.5 体重趋势

| 项目 | 内容 |
|------|------|
| 方法 | `GET` |
| 路径 | `/api/stats/weight-trend` |
| 权限 | 需认证 |

---

### 5. 用户档案模块

基础路径：`/api/profile`

#### 5.1 获取用户档案

| 项目 | 内容 |
|------|------|
| 方法 | `GET` |
| 路径 | `/api/profile` |
| 权限 | 需认证 |

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "gender": "male",
    "age": 25,
    "height": 175.00,
    "weight": 70.00,
    "activityLevel": "moderate",
    "targetWeight": 65.00,
    "targetCalories": 2200
  }
}
```

---

#### 5.2 更新用户档案

| 项目 | 内容 |
|------|------|
| 方法 | `PUT` |
| 路径 | `/api/profile` |
| 权限 | 需认证 |

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `gender` | String | 是 | 性别 |
| `age` | Integer | 是 | 年龄 |
| `height` | Number | 是 | 身高(cm) |
| `weight` | Number | 是 | 体重(kg) |
| `activity` | String | 是 | 活动等级 |
| `targetWeight` | Number | 否 | 目标体重(kg) |

**请求示例：**

```json
{
  "gender": "male",
  "age": 25,
  "height": 175.0,
  "weight": 70.0,
  "activity": "moderate",
  "targetWeight": 65.0
}
```

---

## 权限说明

| 路径 | 权限 |
|------|------|
| `/api/auth/**` | 公开 |
| `/api/foods/**` | 需认证 |
| `/api/records/**` | 需认证 |
| `/api/stats/**` | 需认证 |
| `/api/profile/**` | 需认证 |
| `/swagger-ui.html` | 公开 |
| `/v3/api-docs/**` | 公开 |

---

## 已知待完善项

1. 登录用户 ID 当前在 Controller 层硬编码为 `1L`，后续需接入 JWT 鉴权上下文
2. `SecurityConfig` 尚未接入 JWT 过滤器，仅配置了基础访问规则
3. `StatsServiceImpl` 统计逻辑为骨架，真实聚合 SQL 需后续补充
