# 个人健康饮食计划系统后端骨架

## 1. 当前骨架做了什么

- 使用 `Spring Boot 3 + JDK 17 + Maven` 搭好了可继续开发的 Java 后端工程。
- 按照前端交接文档拆分了 `auth / user / food / record / stats` 五个业务模块。
- 提供了统一返回结构、全局异常处理、JWT 工具类、基础安全配置。
- 把数据库建表语句单独放到了 `sql/schema.sql`，避免 SQL 和 Java 代码混在一起。

## 2. 目录职责说明

### `common`

- `config`：放基础配置，如安全配置、JWT 配置。
- `exception`：放业务异常和全局异常处理。
- `result`：统一接口返回格式。
- `utils`：通用工具类。

### `auth`

- 负责注册、登录、获取当前登录用户信息。
- 当前已经提供 `register / login / me` 接口骨架。

### `user`

- 负责用户档案的读取、更新、建议热量计算。
- `ProfileServiceImpl` 中已经放了基础 TDEE 计算逻辑。

### `food`

- 负责食物库列表、关键字搜索、自定义食物录入。
- 当前响应字段已尽量对齐前端 `FoodLibraryItem`。

### `record`

- 负责每日饮食记录查询、新增记录、删除饮食明细。
- 使用 `diet_record` 主表和 `diet_record_item` 明细表拆分建模。

### `stats`

- 负责今日摄入、本周热量、本周宏量、打卡率、体重趋势等统计接口。
- 当前先保留聚合接口骨架，便于后续基于真实数据继续实现。

## 3. 当前哪些地方还是占位

- 登录用户 ID 目前在 Controller 层写死为 `1L`，后续需要接入真正的 JWT 鉴权上下文。
- `SecurityConfig` 目前只完成了基础访问规则，尚未接入 JWT 过滤器。
- `StatsServiceImpl` 目前是统计骨架，真实聚合 SQL/查询逻辑需要后续补充。
- `RecordController` 里的 `time` 字段当前只保留了前端接口形状，数据库里还未单独存时间。

## 4. 推荐继续开发顺序

1. 执行 `sql/schema.sql` 建库建表。
2. 先跑通注册、登录、档案读写。
3. 再接食物库查询和自定义食物新增。
4. 然后完善饮食记录增删查。
5. 最后补统计查询和 JWT 鉴权过滤器。
