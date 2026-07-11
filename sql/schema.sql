CREATE DATABASE IF NOT EXISTS diet_plan DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE diet_plan;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户主键',
    username VARCHAR(32) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(64) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(128) NOT NULL COMMENT 'BCrypt 加密密码',
    nickname VARCHAR(32) NOT NULL COMMENT '昵称',
    role VARCHAR(16) NOT NULL DEFAULT 'USER' COMMENT '角色：USER-普通用户, ADMIN-管理员',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='系统用户表';

-- 管理员账号由 DataInitializer 在应用启动时自动创建（admin / admin123）

CREATE TABLE IF NOT EXISTS user_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '档案主键',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户 ID',
    gender VARCHAR(16) NOT NULL COMMENT '性别',
    age INT NOT NULL COMMENT '年龄',
    height DECIMAL(5,2) NOT NULL COMMENT '身高 cm',
    weight DECIMAL(5,2) NOT NULL COMMENT '体重 kg',
    activity_level VARCHAR(32) NOT NULL COMMENT '活动等级',
    target_weight DECIMAL(5,2) DEFAULT NULL COMMENT '目标体重 kg',
    target_calories INT DEFAULT NULL COMMENT '建议热量',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) COMMENT='用户档案表';

CREATE TABLE IF NOT EXISTS food (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '食物主键',
    name VARCHAR(64) NOT NULL COMMENT '食物名称',
    category VARCHAR(32) NOT NULL COMMENT '食物分类',
    serving_unit VARCHAR(16) NOT NULL COMMENT '份量单位',
    serving_size DECIMAL(8,2) NOT NULL COMMENT '标准份量',
    calories INT NOT NULL COMMENT '热量 kcal',
    protein DECIMAL(8,2) NOT NULL COMMENT '蛋白质 g',
    carbs DECIMAL(8,2) NOT NULL COMMENT '碳水 g',
    fat DECIMAL(8,2) NOT NULL COMMENT '脂肪 g',
    is_custom TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否用户自定义',
    created_by BIGINT DEFAULT NULL COMMENT '创建者',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_food_user FOREIGN KEY (created_by) REFERENCES sys_user(id)
) COMMENT='食物库表';

CREATE TABLE IF NOT EXISTS diet_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '饮食记录主键',
    user_id BIGINT NOT NULL COMMENT '所属用户',
    record_date DATE NOT NULL COMMENT '记录日期',
    meal_type VARCHAR(16) NOT NULL COMMENT '餐次',
    note VARCHAR(255) DEFAULT NULL COMMENT '备注',
    total_calories INT NOT NULL DEFAULT 0 COMMENT '该餐总热量',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_record_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) COMMENT='饮食记录主表';

CREATE TABLE IF NOT EXISTS diet_record_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '饮食明细主键',
    record_id BIGINT NOT NULL COMMENT '饮食记录 ID',
    food_id BIGINT DEFAULT NULL COMMENT '食物 ID',
    food_name_snapshot VARCHAR(64) NOT NULL COMMENT '食物名称快照',
    amount VARCHAR(32) NOT NULL COMMENT '摄入份量',
    calories INT NOT NULL COMMENT '热量 kcal',
    protein DECIMAL(8,2) NOT NULL COMMENT '蛋白质 g',
    carbs DECIMAL(8,2) NOT NULL COMMENT '碳水 g',
    fat DECIMAL(8,2) NOT NULL COMMENT '脂肪 g',
    CONSTRAINT fk_record_item_record FOREIGN KEY (record_id) REFERENCES diet_record(id),
    CONSTRAINT fk_record_item_food FOREIGN KEY (food_id) REFERENCES food(id)
) COMMENT='饮食记录明细表';

CREATE TABLE IF NOT EXISTS weight_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '体重记录主键',
    user_id BIGINT NOT NULL COMMENT '所属用户',
    record_date DATE NOT NULL COMMENT '记录日期',
    weight DECIMAL(5,2) NOT NULL COMMENT '体重 kg',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_weight_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) COMMENT='体重记录表';
