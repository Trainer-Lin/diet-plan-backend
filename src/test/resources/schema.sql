CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(32) NOT NULL UNIQUE,
    email VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    nickname VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    gender VARCHAR(16) NOT NULL,
    age INT NOT NULL,
    height DECIMAL(5,2) NOT NULL,
    weight DECIMAL(5,2) NOT NULL,
    activity_level VARCHAR(32) NOT NULL,
    target_weight DECIMAL(5,2) DEFAULT NULL,
    target_calories INT DEFAULT NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS food (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    category VARCHAR(32) NOT NULL,
    serving_unit VARCHAR(16) NOT NULL,
    serving_size DECIMAL(8,2) NOT NULL,
    calories INT NOT NULL,
    protein DECIMAL(8,2) NOT NULL,
    carbs DECIMAL(8,2) NOT NULL,
    fat DECIMAL(8,2) NOT NULL,
    is_custom BOOLEAN NOT NULL DEFAULT FALSE,
    created_by BIGINT DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_food_user FOREIGN KEY (created_by) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS diet_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    record_date DATE NOT NULL,
    meal_type VARCHAR(16) NOT NULL,
    note VARCHAR(255) DEFAULT NULL,
    total_calories INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_record_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS diet_record_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    record_id BIGINT NOT NULL,
    food_id BIGINT DEFAULT NULL,
    food_name_snapshot VARCHAR(64) NOT NULL,
    amount VARCHAR(32) NOT NULL,
    calories INT NOT NULL,
    protein DECIMAL(8,2) NOT NULL,
    carbs DECIMAL(8,2) NOT NULL,
    fat DECIMAL(8,2) NOT NULL,
    CONSTRAINT fk_record_item_record FOREIGN KEY (record_id) REFERENCES diet_record(id),
    CONSTRAINT fk_record_item_food FOREIGN KEY (food_id) REFERENCES food(id)
);

CREATE TABLE IF NOT EXISTS weight_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    record_date DATE NOT NULL,
    weight DECIMAL(5,2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_weight_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
);
