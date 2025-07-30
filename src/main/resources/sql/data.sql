INSERT INTO items (id, name, price, created_at)
VALUES (1, '질레트 하얀 면도날', 10000, NOW());

INSERT INTO item_stock(item_id, quantity)
VALUES (1, 50);

INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (1, 1, 0, NOW(), NOW());


-- 쿠폰 1
INSERT INTO coupon (id, item_id, name, discount_amount, valid_from, valid_to, disabled, issued_at, created_at)
VALUES (1, 1, '질레트 하얀 면도날 할인', 2000, '2025-07-01', '2025-08-31', false, NULL, NOW());

INSERT INTO coupon_stock (coupon_id, quantity)
VALUES (1, 2);


-- 쿠폰 2
INSERT INTO coupon (id, item_id, name, discount_amount, valid_from, valid_to, disabled, issued_at, created_at)
VALUES (2, 102, '여름 세일 쿠폰', 3000, '2025-07-15', '2025-09-15', false, NULL, NOW());

INSERT INTO coupon_stock (coupon_id, quantity)
VALUES (2, 50);


-- 쿠폰 3
INSERT INTO coupon (id, item_id, name, discount_amount, valid_from, valid_to, disabled, issued_at, created_at)
VALUES (3, 103, 'VIP 한정 쿠폰', 5000, '2025-07-01', '2025-12-31', false, NULL, NOW());

INSERT INTO coupon_stock (coupon_id, quantity)
VALUES (3, 10);
