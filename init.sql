-- 用户表
CREATE TABLE t_user (
                        id SERIAL PRIMARY KEY,
                        username VARCHAR(50) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        real_name VARCHAR(50),
                        phone VARCHAR(20),
                        role VARCHAR(20) DEFAULT 'USER', -- 'USER' 或 'ADMIN'
                        create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 服务器套餐表
CREATE TABLE t_server (
                          id SERIAL PRIMARY KEY,
                          model VARCHAR(100) NOT NULL,
                          cpu VARCHAR(50),
                          ram VARCHAR(50),
                          disk VARCHAR(100),
                          price_per_month DECIMAL(10,2) NOT NULL,
                          is_available BOOLEAN DEFAULT TRUE
);

-- 租赁订单表
CREATE TABLE t_order (
                         id SERIAL PRIMARY KEY,
                         user_id INT NOT NULL REFERENCES t_user(id) ON DELETE CASCADE,
                         server_id INT NOT NULL REFERENCES t_server(id),
                         months INT NOT NULL,                 -- 租赁月数
                         total_price DECIMAL(10,2) NOT NULL,
                         status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, PAID, CANCELLED, COMPLETED
                         order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入测试管理员和普通用户（密码均为 123456，BCrypt 加密）
INSERT INTO t_user (username, password, real_name, phone, role) VALUES
                                                                    ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '管理员', '13800000000', 'ADMIN'),
                                                                    ('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '张三', '13812345678', 'USER');

-- 插入测试服务器套餐
INSERT INTO t_server (model, cpu, ram, disk, price_per_month) VALUES
                                                                  ('标准型 S1', '2核', '4GB', '40GB SSD', 99.00),
                                                                  ('性能型 P2', '4核', '8GB', '80GB SSD', 199.00),
                                                                  ('企业型 E3', '8核', '16GB', '200GB SSD', 399.00);