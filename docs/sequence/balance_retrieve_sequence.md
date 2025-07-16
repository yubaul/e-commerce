```mermaid
   sequenceDiagram
    participant AccountController
    participant AccountService
    participant AccountRepository

    AccountController ->> AccountService: 잔액 조회 요청 (식별자)

    alt 계좌 없음
        AccountService -->> AccountController: 예외 - 계좌 없음
    else 계좌 존재
        AccountService ->> AccountRepository: 계좌 정보 조회
        AccountRepository -->> AccountService: 계좌 잔액 반환
        AccountService -->> AccountController: 잔액 응답
    end

```