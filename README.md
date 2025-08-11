
# E-Commerce Order System

> 결제 실패까지 책임지는 도메인 중심의 주문 시스템  
> Spring Boot + Clean Architecture + In-Memory DB + TDD 기반 개발

---

## Getting Started

### Prerequisites

로컬 개발 환경(`local` profile)에서 실행하려면 아래 명령어로 Docker 인프라 컨테이너를 실행하세요.

```bash
docker-compose up -d
```

단위 테스트 및 통합 테스트 실행:

```bash
./gradlew test
```

---

## Architecture

본 프로젝트는 클린 아키텍처(Clean Architecture)를 기반으로 구성되어 있으며, 각 계층은 아래와 같은 책임을 가집니다:

```
com.project
├── domain
│   ├── Entity, Command
│   ├── Service, Processor
│   ├── Reader, Store, Factory (interface)
├── application
│   └── Facade / Service: 유스케이스 조합 및 트랜잭션 경계
├── interfaces
│   └── Controller, Request/Response DTO
├── infrastructure
│   ├── DB Adapter: In-Memory Repository 구현체
│   └── External Adapter: 외부 시스템 연동
```

- 도메인 계층은 기술 독립적이며, 외부 구현체에 의존하지 않습니다.
- 의존성은 interfaces → application → domain 방향으로만 흐릅니다.
- 테스트 시 domain 계층은 인프라와 격리되어 검증 가능합니다.

---

## 기능 요약

### 필수 구현 기능

| 도메인    | 기능                                       |
|-----------|--------------------------------------------|
| 상품      | 단일 상품 조회, 인기 상품 TOP5 조회         |
| 주문/결제 | 주문 등록, 재고 차감, 쿠폰 사용, 결제 수행   |
| 포인트    | 잔액 차감, 잔액 부족 시 예외 처리           |

### 주문 등록 핵심 흐름

- 상품 재고 차감 (비관적 락)
- 유저 쿠폰 사용 (낙관적 락)
- 계좌 잔액 차감 (비관적 락)


---

## 동시성 제어 전략

| 도메인     | 전략               | 이유 |
|------------|--------------------|------|
| 계좌 잔액  | 비관적 락          | 금전 손실 방지, 실패 비용 큼 |
| 쿠폰 사용  | 낙관적 락 + 리트라이 | 낮은 충돌 빈도, 높은 성능 |
| 상품 재고  | 비관적 락          | 높은 충돌 가능성, 재시도 부담 큼 |

- REQUIRES_NEW 트랜잭션 전략은 도입하지 않음. 보상 트랜잭션 복잡도와 불필요한 쿠폰 복구 리스크를 회피하기 위해 낙관적 락 + 재시도로 대체
- 충돌 횟수와 도메인 특성을 기준으로 낙관적/비관적 락을 선별 적용

---

## 테스트 전략

- 단위 테스트: Mockito 기반 독립 테스트 (`@Mock`)
- 통합 테스트: Testcontainers 기반 DB 통합 시나리오 포함
- 실패 시나리오: 재고 부족, 잔액 부족, 쿠폰 충돌 등 검증
- 복구 로직 검증 포함

---

## 회고 요약

- 충돌 빈도가 높은 도메인은 비관적 락이 유효
- 충돌 빈도가 낮고 성능 우선 도메인은 낙관적 락 + 재시도 전략 사용
- 낙관적 락 충돌 시 `REQUIRES_NEW`는 오히려 보상 트랜잭션 복잡도를 유발할 수 있음
- 각 도메인 특성을 기준으로 락 전략 선택 기준을 명확히 수립하는 것이 중요
