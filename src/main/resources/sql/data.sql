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

INSERT INTO user_coupon (id, user_id, coupon_id, user_coupon_status, used_at, created_at)
VALUES (1, 10, 200, 'AVAILABLE', NULL, NOW());

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
INSERT INTO user_coupon (id, user_id, coupon_id, user_coupon_status, used_at, created_at)
VALUES (1000, 11, 100, 'USED', NOW(), NOW());

-- 사용 중지된 쿠폰 (couponId = 101)
INSERT INTO coupon (id, item_id, name, discount_amount, valid_from, valid_to, disabled, issued_at, created_at)
VALUES (101, 500, '사용 중지된 쿠폰', 3000, '2025-01-01', '2025-12-31', true, NULL, NOW());

INSERT INTO coupon_stock (coupon_id, quantity, created_at)
VALUES (101, 10, NOW());

INSERT INTO user_coupon (id, user_id, coupon_id, user_coupon_status, used_at, created_at)
VALUES (1001, 11, 101, 'AVAILABLE', NOW(), NOW());




-- 계좌 잔액 충전 동시성 테스트를 위한 계좌
INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (2, 500, 0, NOW(), NOW());


-- 선착순 쿠폰 발급 동시성 테스트
INSERT INTO coupon (id, item_id, name, discount_amount, valid_from, valid_to, disabled, issued_at, created_at)
VALUES (500, 1000, '동시성 테스트용 쿠폰', 2000, '2025-07-01', '2025-08-31', false, NULL, NOW());

INSERT INTO coupon_stock (coupon_id, quantity, created_at)
VALUES (500, 30, NOW());

INSERT INTO coupon (id, item_id, name, discount_amount, valid_from, valid_to, disabled, issued_at, created_at)
VALUES (501, 1000, '동시성 테스트용 쿠폰', 2000, '2025-07-01', '2025-08-31', false, NULL, NOW());

INSERT INTO coupon_stock (coupon_id, quantity, created_at)
VALUES (501, 10, NOW());

INSERT INTO coupon (id, item_id, name, discount_amount, valid_from, valid_to, disabled, issued_at, created_at)
VALUES (502, 1000, '동시성 테스트용 쿠폰', 2000, '2025-07-01', '2025-08-31', false, NULL, NOW());

INSERT INTO coupon_stock (coupon_id, quantity, created_at)
VALUES (502, 10, NOW());


-- 주문 상품 재고 감소 동시성 테스트

-- 동시_주문_요청시_재고_수량만큼만_주문_성공
INSERT INTO items (id, name, price, created_at, updated_at)
VALUES (600, '주문 동시성 테스트 상품', 10000, NOW(), NOW());

INSERT INTO item_stock (item_id, quantity, created_at, updated_at)
VALUES (600, 5, NOW(), NOW());

INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (600, 600, 100000, NOW(), NOW());

INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (601, 601, 100000, NOW(), NOW());

INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (602, 602, 100000, NOW(), NOW());

INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (603, 603, 100000, NOW(), NOW());

INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (604, 604, 100000, NOW(), NOW());

-- 동시_다중상품_중간_재고_품절시_이전_차감_롤백
INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (610, 610, 100000, NOW(), NOW());

INSERT INTO items (id, name, price, created_at, updated_at)
VALUES (610, '주문 동시성 테스트 상품', 10000, NOW(), NOW());
INSERT INTO item_stock (item_id, quantity, created_at, updated_at)
VALUES (610, 1, NOW(), NOW());

INSERT INTO items (id, name, price, created_at, updated_at)
VALUES (611, '주문 동시성 테스트 상품', 10000, NOW(), NOW());
INSERT INTO item_stock (item_id, quantity, created_at, updated_at)
VALUES (611, 1, NOW(), NOW());

INSERT INTO items (id, name, price, created_at, updated_at)
VALUES (612, '주문 동시성 테스트 상품', 10000, NOW(), NOW());
INSERT INTO item_stock (item_id, quantity, created_at, updated_at)
VALUES (612, 1, NOW(), NOW());

INSERT INTO items (id, name, price, created_at, updated_at)
VALUES (613, '주문 동시성 테스트 상품', 10000, NOW(), NOW());
INSERT INTO item_stock (item_id, quantity, created_at, updated_at)
VALUES (613, 1, NOW(), NOW());

INSERT INTO items (id, name, price, created_at, updated_at)
VALUES (614, '주문 동시성 테스트 상품', 10000, NOW(), NOW());
INSERT INTO item_stock (item_id, quantity, created_at, updated_at)
VALUES (614, 1, NOW(), NOW());


-- 동시_주문_요청시_재고_초과_예외

INSERT INTO items (id, name, price, created_at, updated_at)
VALUES (650, '주문 동시성 테스트 상품', 10000, NOW(), NOW());

INSERT INTO item_stock (item_id, quantity, created_at, updated_at)
VALUES (650, 4, NOW(), NOW());

INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (650, 650, 100000, NOW(), NOW());

INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (651, 651, 100000, NOW(), NOW());

INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (652, 652, 100000, NOW(), NOW());

INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (653, 653, 100000, NOW(), NOW());

INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (654, 654, 100000, NOW(), NOW());

-- 동시_잔액_차감_시_한도_이내만_성공
INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (660, 660, 30000, NOW(), NOW());

INSERT INTO items (id, name, price, created_at, updated_at)
VALUES (660, '주문 동시성 테스트 상품', 10000, NOW(), NOW());

INSERT INTO item_stock (item_id, quantity, created_at, updated_at)
VALUES (660, 5, NOW(), NOW());


-- 동시_쿠폰_사용_요청_시_하나만_할인_적용되고_나머지는_정가로_주문됨
INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (670, 670, 100000, NOW(), NOW());

INSERT INTO items (id, name, price, created_at, updated_at)
VALUES (670, '주문 동시성 테스트 상품', 10000, NOW(), NOW());

INSERT INTO item_stock (item_id, quantity, created_at, updated_at)
VALUES (670, 5, NOW(), NOW());

INSERT INTO coupon (id, item_id, name, discount_amount, valid_from, valid_to, disabled, issued_at, created_at)
VALUES (670, 670, '동시성 테스트용 쿠폰', 5000, '2025-07-01', '2025-08-31', false, NULL, NOW());

INSERT INTO coupon_stock (coupon_id, quantity, created_at)
VALUES (670, 100, NOW());

INSERT INTO user_coupon (id, user_id, coupon_id, user_coupon_status, used_at, created_at)
VALUES (670, 670, 670, 'AVAILABLE', NOW(), NOW());


-- 결제_실패시_쿠폰_해제_및_재고_복구_그리고_주문_상태_FAILED
INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (680, 680, 10000, NOW(), NOW());

INSERT INTO items (id, name, price, created_at, updated_at)
VALUES (680, '주문 동시성 테스트 상품', 20000, NOW(), NOW());

INSERT INTO item_stock (item_id, quantity, created_at, updated_at)
VALUES (680, 5, NOW(), NOW());

INSERT INTO coupon (id, item_id, name, discount_amount, valid_from, valid_to, disabled, issued_at, created_at)
VALUES (680, 680, '동시성 테스트용 쿠폰', 5000, '2025-07-01', '2025-08-31', false, NULL, NOW());

INSERT INTO coupon_stock (coupon_id, quantity, created_at)
VALUES (680, 100, NOW());

INSERT INTO user_coupon (id, user_id, coupon_id, user_coupon_status, used_at, created_at)
VALUES (680, 680, 680, 'AVAILABLE', NOW(), NOW());