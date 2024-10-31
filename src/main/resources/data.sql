--고객번호 및 초기잔액 생성
INSERT INTO BALANCE (id,user_id, total_balance)
values ( 1, 1001, 100000 );

--상품 목록 생성
INSERT INTO PRODUCT (id, name, price, sales)
VALUES
    (1, '컴퓨터', 100000, 3),
    (2, '마우스', 5000, 5),
    (3, '가방', 20000, 1),
    (4, '상의', 10000, 1),
    (5, '치마', 10000, 1),
    (6, '바지', 10000, 9),
    (7, '양말', 1000, 7),
    (8, '신발', 50000, 2),
    (9, '모자', 10000, 8),
    (10, '목걸이', 30000, 4);

--상품 재고 생성
INSERT INTO PRODUCT_STOCK (ID, PRODUCT_ID, STOCK, UPDATE_DATE)
VALUES ( 1,1,10,NOW()),
       ( 2,2,10, NOW()),
       ( 3,3,10, NOW()),
       ( 4,4,10, NOW()),
       ( 5,5,10, NOW()),
       ( 6,6,10, NOW()),
       ( 7,7,10, NOW()),
       ( 8,8,10, NOW()),
       ( 9,9,10, NOW()),
       ( 10,10,10, NOW());
