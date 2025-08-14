DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL
);

DROP TABLE IF EXISTS account_history;
CREATE TABLE account_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT,
    amount INT NOT NULL,
    balance INT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    source_type VARCHAR(20) NOT NULL,
    payment_id BIGINT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

DROP TABLE IF EXISTS accounts;
CREATE TABLE accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    balance INT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);


DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    total_amount INT NOT NULL,
    order_status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

DROP TABLE IF EXISTS order_items;
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT,
    item_id BIGINT,
    quantity INT NOT NULL,
    item_price_at_order INT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

DROP TABLE IF EXISTS items;
CREATE TABLE items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL
);

DROP TABLE IF EXISTS item_stock;
CREATE TABLE item_stock (
    item_id BIGINT PRIMARY KEY,
    quantity INT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL
);

DROP TABLE IF EXISTS payment;
CREATE TABLE payment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT,
    pay_method VARCHAR(20) NOT NULL,
    pay_amount INT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

DROP TABLE IF EXISTS coupon;
CREATE TABLE coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_id BIGINT,
    name VARCHAR(255) NOT NULL,
    discount_amount INT NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE NOT NULL,
    disabled BOOLEAN NOT NULL,
    issued_at DATETIME ,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL
);

DROP TABLE IF EXISTS coupon_stock;
CREATE TABLE coupon_stock (
    coupon_id BIGINT PRIMARY KEY,
    quantity INT NOT NULL default 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL
);

DROP TABLE IF EXISTS user_coupon;
CREATE TABLE user_coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    order_id BIGINT,
    user_coupon_status VARCHAR(20) NOT NULL,
    used_at DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL
);

ALTER TABLE user_coupon ADD CONSTRAINT uk_user_coupon UNIQUE (user_id, coupon_id);