
# DB 성능 개선 보고서

## 1. 성능 저하 의심 기능 도출 기준

다음 다섯 가지 항목 중 최소 두 가지 이상에 해당하는 기능을 **잠재적 성능 저하 대상**으로 분류하였습니다:

1. **고속 증가하는 대용량 테이블인지 여부**
  - 주문, 주문상품, 포인트 및 쿠폰 관련 테이블은 시간이 지날수록 누적 데이터가 빠르게 증가합니다.
  - 특히 삭제 없이 기록만 추가되는 로그성 테이블은 Full Scan 시 심각한 성능 저하를 유발할 수 있습니다.
  - 예시: `order_item`, `user_history`, `coupon_user` 등

2. **조회 조건이 단순하지 않은가?**
  - 단일 키 필터링이 아닌 범위, 다중 조건, 상태 필터 등이 포함된 WHERE절은 인덱스 활용을 어렵게 합니다.
  - 예: `BETWEEN`, `IN`, `OR`, 복합 조건 조합

3. **정렬 및 LIMIT 구문 포함 여부**
  - `ORDER BY` 절은 종종 `FILESORT` 연산을 발생시켜 부하를 유발합니다.
  - `LIMIT`은 정렬과 결합될 경우 성능에 더 큰 영향을 줄 수 있습니다.
  - 예: `ORDER BY created_at DESC LIMIT 20`

4. **다중 테이블 조인이 필요한가?**
  - 조인은 설계에 따라 성능에 치명적일 수 있습니다.
  - 특히 다대일 조인과 함께 N+1 쿼리 문제가 동반되면 과도한 I/O를 유발합니다.

5. **API 호출 빈도가 높은가?**
  - 사용자의 마이페이지, 주문 이력, 쿠폰 조회 등 자주 호출되는 API는 개별 쿼리가 가볍더라도 누적 부하가 큽니다.

---

## 2. 위험 기능별 분류 결과

| 기능 구분          | 기준 충족 항목 |
|----------------|---------------------------------------------------|
| 유저 주문 내역 조회    | 로그성 대량 테이블, 정렬, 반복 호출 API |
| 주문 상세 데이터 조회   | 다중 테이블 조인, N+1 가능성, 응답 필드 다양 |
| 계좌 이력/쿠폰 이력 조회 | 범위 조건, 시간 정렬, 자주 호출되는 필터링 기반 API |

---

## 3. 기능별 성능 저하 분석 요약

| 대상 기능          | 병목 원인                                 |
|----------------|---------------------------------------|
| 유저 주문 목록 조회    | `user_id` 필터 + 정렬 조합, 대량 데이터 탐색 부담    |
| 주문 상세 보기       | `order_item` → `item` 조인으로 인한 N+1 가능성 |
| 계좌 이력/쿠폰 내역 조회 | 정렬, 복합 필터 조건으로 인한 I/O 증가, paging 필요성  |

---

## 4. 성능 개선을 위한 쿼리 및 인덱스 리팩토링

###  주문 목록 조회 쿼리 개선

**인덱스 생성**
```sql
CREATE INDEX idx_orders_user_created_at 
ON orders(user_id, created_at DESC);
```

**기존 쿼리**
```sql
SELECT * FROM orders
WHERE user_id = ?
ORDER BY created_at DESC
LIMIT 20;
```

**개선된 형태**
```sql
SELECT order_id, status, created_at
FROM orders
WHERE user_id = ?
ORDER BY created_at DESC
LIMIT 20;
```

>목적: 인덱스 커버리지 향상 + I/O 부하 감소

---

### 주문 상세 조회 개선 방향

- 상품 정보는 조인 대신 `OrderItem` 테이블에 주문 시점 기준 가격/이름을 반정규화하여 저장
- 조인 연산 최소화 및 불필요한 쿼리 제거
- 상품 정보 일관성 보장 (당시 정보 기준)

---


### 계좌 내역 / 유저 쿠폰 테이블 최적화

**인덱스 적용**
```sql
CREATE INDEX idx_account_history_accid_createdat
ON account_history(account_id, created_at DESC);

CREATE INDEX idx_user_coupon_user_coupon_used
ON user_coupon(user_id, coupon_id, used);
```

- 계좌 내역은 `account_id` + `created_at DESC` 복합 인덱스를 통해 최근 거래 내역 조회 성능을 향상시킬 수 있음
- 쿠폰 조회 시 자주 사용하는 조건 (`user_id`, `coupon_id`, `used`) 기준으로 복합 인덱스를 구성
- 정렬 필드를 포함한 인덱스로 `ORDER BY created_at DESC LIMIT 50` 형태의 쿼리 최적화
- 페이징 쿼리는 커서 방식 (keyset pagination) 적용 시 인덱스 효율 극대화
- 오래된 거래 기록은 `archive_account_history` 등 별도 테이블로 분리 보관 고려 가능


## 5. EXPLAIN 실행 계획 비교 (account_history 기준)

### 인덱스 적용 전 (총 데이터 100,000건)
```sql
EXPLAIN SELECT * FROM account_history
WHERE account_id = 123
  AND created_at BETWEEN '2025-01-01' AND '2025-07-30'
ORDER BY created_at DESC
LIMIT 50;
```

| 항목 | 내용 | 해석 |
|------|------|------|
| type | ALL | 테이블 전체 탐색 (Full Table Scan) 발생 |
| key | NULL | 인덱스 전혀 미사용 |
| rows | 100000 | 전체 레코드 탐색으로 성능 저하 |
| Extra | Using where; Using filesort | 정렬 시 디스크 기반 정렬 발생, I/O 부하 유발 |

---

###  인덱스 적용 후 (복합 인덱스 활용)
```sql
CREATE INDEX idx_account_history_accid_createdat
ON account_history(account_id, created_at DESC);
```

| 항목 | 내용 | 개선 효과 |
|------|------|------------|
| type | range | 조건 기반 인덱스 범위 조회로 변경 |
| key | idx_account_history_accid_createdat | 인덱스 정상 활용 |
| rows | 50~100 | 조건 만족 범위만 탐색 |
| Extra | Using index condition; Using where | 인덱스 필터링 + 정렬 최적화로 Filesort 제거 가능 |

---

##  요약

- `account_history`는 사용자별 거래가 지속적으로 누적되는 로그성 테이블로, **10만 건 이상의 데이터가 저장**되어 있음
- 기존 쿼리는 전체 탐색 + 정렬 수행으로 매우 비효율적
- `account_id`, `created_at DESC` 복합 인덱스를 적용하면 **범위 조건 + 정렬까지 커버**
- 결과적으로 `LIMIT N` 쿼리에 최적화된 인덱스를 구성할 수 있음
- `filesort` 제거로 I/O 비용 감소, 쿼리 응답 시간 단축 효과
