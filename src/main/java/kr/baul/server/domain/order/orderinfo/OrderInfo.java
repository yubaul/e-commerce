package kr.baul.server.domain.order.orderinfo;

import lombok.Builder;

import java.time.LocalDateTime;

public class OrderInfo {

    @Builder
    public record Order(
            Long id,
            Long userId,
            Long totalAmount,
            LocalDateTime createdAt
    ){

    }

}
