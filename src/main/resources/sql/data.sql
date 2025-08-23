INSERT INTO items (id, name, price, created_at)
VALUES (1, '질레트 하얀 면도날', 10000, NOW());

INSERT INTO item_stock(item_id, quantity, created_at)
VALUES (1, 50, NOW());

INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (1, 1, 0, NOW(), NOW());

INSERT INTO accounts (id, user_id, balance, created_at, updated_at)
VALUES (999999, 999999, 1000000, NOW(), NOW());


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





-- 인기 상품 조회
-- 아이템 10개
INSERT INTO items (id, name, price, created_at, updated_at) VALUES
(2000, 'Item A', 1000, NOW(), NOW()),
(2001, 'Item B', 1500, NOW(), NOW()),
(2002, 'Item C', 2000, NOW(), NOW()),
(2003, 'Item D', 2500, NOW(), NOW()),
(2004, 'Item E', 3000, NOW(), NOW()),
(2005, 'Item F', 3500, NOW(), NOW()),
(2006, 'Item G', 4000, NOW(), NOW()),
(2007, 'Item H', 4500, NOW(), NOW()),
(2008, 'Item I', 5000, NOW(), NOW()),
(2009, 'Item J', 5500, NOW(), NOW());

-- 주문 30건
INSERT INTO orders (id, user_id, total_amount, order_status, created_at, updated_at) VALUES
(2100, 1, 5000,  'PAID',     DATE_ADD(NOW(), INTERVAL -1 DAY), NOW()),
(2101, 2, 3000,  'PAID',     DATE_ADD(NOW(), INTERVAL -2 DAY), NOW()),
(2102, 3, 4500,  'CREATED',  DATE_ADD(NOW(), INTERVAL -3 DAY), NOW()),
(2103, 4, 7000,  'PAID',     DATE_ADD(NOW(), INTERVAL -4 DAY), NOW()),
(2104, 5, 2500,  'FAILED',   DATE_ADD(NOW(), INTERVAL -5 DAY), NOW()),
(2105, 6, 8000,  'PAID',     DATE_ADD(NOW(), INTERVAL -6 DAY), NOW()),
(2106, 7, 1500,  'CANCELED', DATE_ADD(NOW(), INTERVAL -7 DAY), NOW()),
(2107, 8, 9000,  'PAID',     DATE_ADD(NOW(), INTERVAL -8 DAY), NOW()),
(2108, 9, 5000,  'PAID',     DATE_ADD(NOW(), INTERVAL -9 DAY), NOW()),
(2109, 10, 2000, 'PAID',     DATE_ADD(NOW(), INTERVAL -10 DAY), NOW()),
(2110, 11, 3000, 'PAID',     DATE_ADD(NOW(), INTERVAL -2 DAY), NOW()),
(2111, 12, 4500, 'CREATED',  DATE_ADD(NOW(), INTERVAL -3 DAY), NOW()),
(2112, 13, 7000, 'PAID',     DATE_ADD(NOW(), INTERVAL -4 DAY), NOW()),
(2113, 14, 2500, 'FAILED',   DATE_ADD(NOW(), INTERVAL -5 DAY), NOW()),
(2114, 15, 8000, 'PAID',     DATE_ADD(NOW(), INTERVAL -6 DAY), NOW()),
(2115, 16, 1500, 'CANCELED', DATE_ADD(NOW(), INTERVAL -7 DAY), NOW()),
(2116, 17, 9000, 'PAID',     DATE_ADD(NOW(), INTERVAL -8 DAY), NOW()),
(2117, 18, 5000, 'PAID',     DATE_ADD(NOW(), INTERVAL -9 DAY), NOW()),
(2118, 19, 2000, 'PAID',     DATE_ADD(NOW(), INTERVAL -10 DAY), NOW()),
(2119, 20, 6000, 'PAID',     DATE_ADD(NOW(), INTERVAL -1 DAY), NOW()),
(2120, 21, 4500, 'PAID',     DATE_ADD(NOW(), INTERVAL -2 DAY), NOW()),
(2121, 22, 7000, 'PAID',     DATE_ADD(NOW(), INTERVAL -3 DAY), NOW()),
(2122, 23, 2500, 'FAILED',   DATE_ADD(NOW(), INTERVAL -4 DAY), NOW()),
(2123, 24, 8000, 'PAID',     DATE_ADD(NOW(), INTERVAL -5 DAY), NOW()),
(2124, 25, 1500, 'CANCELED', DATE_ADD(NOW(), INTERVAL -6 DAY), NOW()),
(2125, 26, 9000, 'PAID',     DATE_ADD(NOW(), INTERVAL -7 DAY), NOW()),
(2126, 27, 5000, 'PAID',     DATE_ADD(NOW(), INTERVAL -8 DAY), NOW()),
(2127, 28, 2000, 'PAID',     DATE_ADD(NOW(), INTERVAL -9 DAY), NOW()),
(2128, 29, 6000, 'PAID',     DATE_ADD(NOW(), INTERVAL -10 DAY), NOW()),
(2129, 30, 4000, 'PAID',     DATE_ADD(NOW(), INTERVAL -1 DAY), NOW());


-- 주문아이템
INSERT INTO order_items (id, order_id, item_id, quantity, item_price_at_order, created_at, updated_at) VALUES
(2200, 2100, 2000, 2, 1000, NOW(), NOW()),
(2201, 2100, 2001, 1, 1500, NOW(), NOW()),
(2202, 2100, 2002, 1, 2000, NOW(), NOW()),
(2203, 2101, 2003, 1, 2500, NOW(), NOW()),
(2204, 2101, 2004, 2, 3000, NOW(), NOW()),
(2205, 2102, 2000, 3, 1000, NOW(), NOW()),
(2206, 2103, 2005, 2, 3500, NOW(), NOW()),
(2207, 2103, 2006, 1, 4000, NOW(), NOW()),
(2208, 2104, 2007, 2, 4500, NOW(), NOW()),
(2209, 2105, 2008, 3, 5000, NOW(), NOW()),
(2210, 2106, 2009, 1, 5500, NOW(), NOW()),
(2211, 2107, 2000, 4, 1000, NOW(), NOW()),
(2212, 2107, 2001, 2, 1500, NOW(), NOW()),
(2213, 2108, 2002, 3, 2000, NOW(), NOW()),
(2214, 2109, 2003, 2, 2500, NOW(), NOW()),
(2215, 2110, 2004, 1, 3000, NOW(), NOW()),
(2216, 2110, 2005, 1, 3500, NOW(), NOW()),
(2217, 2111, 2006, 2, 4000, NOW(), NOW()),
(2218, 2112, 2007, 3, 4500, NOW(), NOW()),
(2219, 2113, 2008, 1, 5000, NOW(), NOW()),
(2220, 2114, 2009, 2, 5500, NOW(), NOW()),
(2221, 2115, 2000, 2, 1000, NOW(), NOW()),
(2222, 2116, 2001, 1, 1500, NOW(), NOW()),
(2223, 2117, 2002, 4, 2000, NOW(), NOW()),
(2224, 2118, 2003, 2, 2500, NOW(), NOW()),
(2225, 2119, 2004, 1, 3000, NOW(), NOW()),
(2226, 2120, 2005, 3, 3500, NOW(), NOW()),
(2227, 2121, 2006, 2, 4000, NOW(), NOW()),
(2228, 2122, 2007, 4, 4500, NOW(), NOW()),
(2229, 2123, 2008, 1, 5000, NOW(), NOW()),
(2230, 2124, 2009, 1, 5500, NOW(), NOW()),
(2231, 2125, 2000, 3, 1000, NOW(), NOW()),
(2232, 2126, 2001, 2, 1500, NOW(), NOW()),
(2233, 2127, 2002, 1, 2000, NOW(), NOW()),
(2234, 2128, 2003, 4, 2500, NOW(), NOW()),
(2235, 2129, 2004, 2, 3000, NOW(), NOW());



-- 인기상품 조회 상품 데이터
INSERT INTO items (id, name, price, created_at, updated_at) VALUES
(2300, 'Item A', 1000, NOW(), NOW()),
(2301, 'Item B', 1500, NOW(), NOW()),
(2302, 'Item C', 2000, NOW(), NOW()),
(2303, 'Item D', 2500, NOW(), NOW()),
(2304, 'Item E', 3000, NOW(), NOW()),
(2305, 'Item F', 1200, NOW(), NOW()),
(2306, 'Item G', 1700, NOW(), NOW()),
(2307, 'Item H', 2200, NOW(), NOW()),
(2308, 'Item I', 2700, NOW(), NOW()),
(2309, 'Item J', 3200, NOW(), NOW()),
(2310, 'Item K', 1800, NOW(), NOW()),
(2311, 'Item L', 2300, NOW(), NOW()),
(2312, 'Item M', 2800, NOW(), NOW()),
(2313, 'Item N', 3300, NOW(), NOW()),
(2314, 'Item O', 3800, NOW(), NOW()),
(2315, 'Item P', 1400, NOW(), NOW()),
(2316, 'Item Q', 1900, NOW(), NOW()),
(2317, 'Item R', 2400, NOW(), NOW()),
(2318, 'Item S', 2900, NOW(), NOW()),
(2319, 'Item T', 3400, NOW(), NOW());