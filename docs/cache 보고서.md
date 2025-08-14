## Cache 보고서

### 1. 개요
최근 3일 동안 가장 많이 팔린 상위 5개 상품을 조회하는 인기상품조회 API(`GET /api/v1/items/top-selling`)는 집계성 쿼리 특성상 조회 비용이 높고,
동일한 결과가 짧은 시간 내 반복 요청되는 경향이 있어 캐싱 적용 대상에 적합하다고 판단하였습니다.

### 2. 캐시 전략 선정
- **전략 유형**: Cache-Aside(Look-Aside)
- **백엔드**: Redis (RedissonSpringCacheManager)
- **TTL**: 10분
- **키 설계**:
    - 캐시 이름: `topSellingItems.v1.10m`
    - 키 패턴: `topSelling:last3days:{yyyy-MM-dd}:limit=5`
    - 구성 요소:
        - **도메인**: `topSelling` – 캐시 데이터 의미 명확화
        - **기간**: `last3days` – 조회 범위 명시
        - **날짜**: `{yyyy-MM-dd}` – 캐시 생성 기준일
        - **조건**: `limit=5` – 쿼리 조건 포함
    - **버전 관리**: 로직 변경 시 `.v2`로 버전 업하여 전 캐시 무효화
- **빈 값 캐싱 방지**: `unless = "#result == null || #result.isEmpty()"`

### 3. 적용 방법
- `@Cacheable`을 사용해 API 호출 시 캐시를 우선 조회
- 미스 시 DB를 조회하고, 결과를 Redis Hash에 저장
- Hash 구조 저장 방식:
  ```
  Redis Key    : topSellingItems.v1.10m
  Hash Field   : topSelling:last3days:2025-08-15:limit=5
  Hash Value   : 직렬화된 List<ItemInfo.TopSelling>
  ```

### 4. 무효화 전략
- TTL 만료를 통한 자동 무효화
- 집계 로직 변경 시 캐시 버전 변경(`v1` → `v2`)으로 전체 무효화

### 5. 기대 효과
- 인기상품조회 API의 반복 조회 시 DB 부하 감소
- TTL을 통한 최신데이터-성능 균형 유지
- 명확한 키 네이밍과 버전 관리로 유지보수 용이성 확보
