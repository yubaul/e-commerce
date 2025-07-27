#  E-Commerce Order System

> 결제 실패까지 책임지는 도메인 중심의 주문 시스템  
> **Spring Boot + Clean Architecture + In-Memory DB + TDD 기반 개발**

---

##  Getting Started

### Prerequisites

####  Running Docker Containers

로컬 개발 환경(`local` profile)에서 실행하려면 아래의 명령어로 Docker 인프라 컨테이너를 실행하세요.

```bash
docker-compose up -d
```

---

## :bookmark_tabs: Architecture

본 프로젝트는 **클린 아키텍처(Clean Architecture)** 를 기반으로 구성되어 있으며, 각 계층은 아래와 같은 책임을 가집니다:

```
📦 com.project
├── 📁 domain
│   ├── Entity, Command
│   ├── Service, Processor
│   ├── Reader, Store, Factory (interface)
├── 📁 application
│   └── Facade / Service: 유스케이스 조합 및 트랜잭션 경계
├── 📁 interfaces
│   └── Controller, Request/Response DTO
├── 📁 infrastructure
│   ├── DB Adapter: In-Memory Repository 구현체
│   └── External Adapter: 외부 시스템 연동 (예: 주문 데이터 플랫폼 전송 )
```

- 도메인 계층은 기술 독립적이며, 외부 구현체에 의존하지 않습니다.
- 의존성은 `interfaces -> application -> domain` 방향으로만 흐릅니다.
- 테스트 시 domain 계층은 인프라와 격리되어 검증 가능합니다.

---

##  핵심 기능 개발

###  필수 구현 기능

| 도메인    | 기능                                   |
|-----------|----------------------------------------|
| 상품      | 단일 상품 조회, 인기 상품 TOP5 조회     |
| 주문/결제 | 주문 등록, 재고 차감, 쿠폰 사용, 결제 수행 |
| 포인트    | 잔액 차감, 잔액 부족 시 예외 처리       |

###  실패 복구 전략

- 결제 실패 시 다음 항목을 복구합니다:
    - 주문 상태: `FAILED`로 전환
    - 재고: 복구 메서드로 수량 복구
    - 쿠폰: 사용 여부 복원 및 저장

###  주문 등록 핵심 로직

```java
try {
    itemStockProcessor.deduct(orderItems);
    Order order = orderFactory.store(userId, orderItems);
    paymentProcessor.process(account, order);
    return orderInfoMapper.toInfo(order);
} catch (Exception e) {
    itemStockProcessor.restore(orderItems);
    restoreUsedCoupons(userId, orderItems);
    order.setOrderStatus(Order.OrderStatus.FAILED);
    orderStore.store(order);
    throw new PaymentFailedException();
}
```

---

##  테스트 전략

- 단위 테스트: Mockito 기반의 독립 테스트 (`@Mock` 활용)
- 실패 시나리오 포함: 잔액 부족, 재고 부족, 쿠폰 예외 등
- 복구 동작 확인 포함



