-- 유저 테이블
CREATE TABLE USERS (
    id LONG PRIMARY KEY,
    status VARCHAR(20),
    create_date TIMESTAMP,
    update_date TIMESTAMP
);

-- 잔액 테이블
CREATE TABLE BALANCE (
    id LONG PRIMARY KEY AUTO_INCREMENT,
    user_id LONG,
    total_balance INTEGER
);

-- 상품 테이블
CREATE TABLE PRODUCT (
    id LONG PRIMARY KEY,
    name VARCHAR(50),
    price INTEGER,
    sales INTEGER,
    update_date TIMESTAMP
);

-- 상품 재고 테이블
CREATE TABLE PRODUCT_STOCK (
    id LONG PRIMARY KEY,
    product_id LONG,
    stock INTEGER,
    update_date TIMESTAMP
);

-- 주문 테이블
CREATE TABLE ORDERS (
    id LONG PRIMARY KEY,
    user_id LONG,
    total_price INTEGER,
    status VARCHAR(20),
    create_date TIMESTAMP,
    update_date TIMESTAMP
);

-- 주문 상품 테이블
CREATE TABLE ORDERS_ITEM (
    id LONG PRIMARY KEY,
    orders_id LONG,
    product_id LONG,
    quantity INTEGER,
    price INTEGER,
    create_date TIMESTAMP
);

-- 결제 테이블
CREATE TABLE PAYMENT (
    id LONG PRIMARY KEY,
    orders_id LONG,
    tx_key VARCHAR(50),
    amount INTEGER,
    status VARCHAR(20),
    create_date TIMESTAMP
);

-- 채권 테이블
CREATE TABLE BOND (
    id LONG PRIMARY KEY,
    bond_key VARCHAR(50),
    orders_id LONG,
    status VARCHAR(20),
    amount INTEGER,
    issued_date TIMESTAMP,
    settled_date TIMESTAMP
);

-- 장바구니 테이블
CREATE TABLE CART (
    id LONG PRIMARY KEY,
    user_id LONG,
    create_date TIMESTAMP,
    update_date TIMESTAMP
);

-- 장바구니 제품 테이블
CREATE TABLE CART_ITEM (
    id LONG PRIMARY KEY,
    cart_id LONG,
    quantity INTEGER,
    create_date TIMESTAMP,
    update_date TIMESTAMP
);