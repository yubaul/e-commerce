```mermaid
sequenceDiagram
    participant CouponController
    participant CouponService
    participant CouponRepository

    CouponController ->> CouponService: 보유 쿠폰 목록 조회 요청 (사용자 ID)
    CouponService ->> CouponRepository: 사용자 ID로 쿠폰 목록 조회
    CouponRepository -->> CouponService: 보유 쿠폰 리스트 반환

    alt 쿠폰 없음
        CouponService -->> CouponController: 빈 리스트 반환
    else 쿠폰 존재
        CouponService -->> CouponController: 쿠폰 리스트 응답
    end


```