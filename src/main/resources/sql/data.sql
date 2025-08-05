INSERT INTO items (id, name, price, created_at)
VALUES (1, '질레트 하얀 면도날', 10000, NOW());

INSERT INTO item_stock(item_id, quantity, created_at)
VALUES (1, 50, NOW());

INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (1, 1, 0, NOW(), NOW());


-- 쿠폰 1
INSERT INTO coupon (id, item_id, name, discount_amount, valid_from, valid_to, disabled, issued_at, created_at)
VALUES (1, 1, '질레트 하얀 면도날 할인', 2000, '2025-07-01', '2025-08-31', false, NULL, NOW());

INSERT INTO coupon_stock (coupon_id, quantity, created_at)
VALUES (1, 2, NOW());


-- 쿠폰 2
INSERT INTO coupon (id, item_id, name, discount_amount, valid_from, valid_to, disabled, issued_at, created_at)
VALUES (2, 102, '여름 세일 쿠폰', 3000, '2025-07-15', '2025-09-15', false, NULL, NOW());

INSERT INTO coupon_stock (coupon_id, quantity, created_at)
VALUES (2, 50, NOW());


-- 쿠폰 3
INSERT INTO coupon (id, item_id, name, discount_amount, valid_from, valid_to, disabled, issued_at, created_at)
VALUES (3, 103, 'VIP 한정 쿠폰', 5000, '2025-07-01', '2025-12-31', false, NULL, NOW());

INSERT INTO coupon_stock (coupon_id, quantity, created_at)
VALUES (3, 10, NOW());








-- 쿠폰 통합 테스트

-- 테스트용 유저 추가
INSERT INTO users (id, name, created_at)
VALUES (10, '통합테스트유저', NOW());

-- 테스트용 계정
INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (10, 10, 10000, NOW(), NOW());

-- 테스트용 아이템
INSERT INTO items (id, name, price, created_at, updated_at)
VALUES (1000, '통합테스트용 상품', 5000, NOW(), NOW());

INSERT INTO item_stock (item_id, quantity, created_at, updated_at)
VALUES (1000, 10, NOW(), NOW());

-- 쿠폰 200: 이미 발급된 쿠폰
INSERT INTO coupon (id, item_id, name, discount_amount, valid_from, valid_to, disabled, issued_at, created_at)
VALUES (200, 1000, '중복 테스트용 쿠폰', 1000, '2025-07-01', '2025-08-31', false, NULL, NOW());

INSERT INTO coupon_stock (coupon_id, quantity, created_at)
VALUES (200, 5, NOW());

INSERT INTO user_coupon (id, user_id, coupon_id, used, used_at, created_at)
VALUES (1, 10, 200, false, NULL, NOW());

-- 쿠폰 201: 새로 발급할 수 있는 쿠폰
INSERT INTO coupon (id, item_id, name, discount_amount, valid_from, valid_to, disabled, issued_at, created_at)
VALUES (201, 1000, '발급 테스트용 쿠폰', 2000, '2025-07-01', '2025-08-31', false, NULL, NOW());

INSERT INTO coupon_stock (coupon_id, quantity, created_at)
VALUES (201, 5, NOW());


-- 계좌 통합 테스트
-- 테스트 계좌
INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (20, 20, 10000, NOW(), NOW());

INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (30, 40, 0, NOW(), NOW());


-- 주문/결제 통합 테스트
-- [1] 아이템 및 재고
INSERT INTO items (id, name, price, created_at, updated_at)
VALUES (500, '테스트 상품', 10000, NOW(), NOW());

INSERT INTO item_stock (item_id, quantity, created_at, updated_at)
VALUES (500, 10, NOW(), NOW());

INSERT INTO items (id, name, price, created_at, updated_at)
VALUES (501, '재고 부족 상품', 10000, NOW(), NOW());

INSERT INTO item_stock (item_id, quantity, created_at, updated_at)
VALUES (501, 2, NOW(), NOW());

-- 재고 부족 테스트를 위한 가상 상품 (없는 ID로 예외 유도)
-- (ID = 999는 INSERT 하지 않음)

-- [2] 계좌
-- 유저 1L → 충분한 금액
INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (100, 11, 1000000, NOW(), NOW());

-- 유저 2L → 금액 부족
INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (200, 12, 100, NOW(), NOW());

-- [3] 쿠폰
-- 이미 사용된 쿠폰 (couponId = 100)
INSERT INTO coupon (id, item_id, name, discount_amount, valid_from, valid_to, disabled, issued_at, created_at)
VALUES (100, 500, '이미 사용된 쿠폰', 2000, '2025-01-01', '2025-12-31', false, NOW(), NOW());

INSERT INTO coupon_stock (coupon_id, quantity, created_at)
VALUES (100, 10, NOW());

-- 쿠폰 발급 및 사용 처리
INSERT INTO user_coupon (id, user_id, coupon_id, used, used_at, created_at)
VALUES (1000, 11, 100, true, NOW(), NOW());

-- 사용 중지된 쿠폰 (couponId = 101)
INSERT INTO coupon (id, item_id, name, discount_amount, valid_from, valid_to, disabled, issued_at, created_at)
VALUES (101, 500, '사용 중지된 쿠폰', 3000, '2025-01-01', '2025-12-31', true, NULL, NOW());

INSERT INTO coupon_stock (coupon_id, quantity, created_at)
VALUES (101, 10, NOW());

INSERT INTO user_coupon (id, user_id, coupon_id, used, used_at, created_at)
VALUES (1001, 11, 101, false, NOW(), NOW());




-- 계좌 잔액 충전 동시성 테스트를 위한 계좌
INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (2, 500, 0, NOW(), NOW());