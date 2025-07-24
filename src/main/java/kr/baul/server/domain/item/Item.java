package kr.baul.server.domain.item;

import kr.baul.server.common.exception.OutOfStockException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Item {
    private Long id;
    private String name;
    private Long price;
    private int quantity;
    private LocalDateTime createdAt;

    @Builder
    public Item(
            Long id,
            String name,
            Long price,
            int quantity
    ){
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.createdAt = LocalDateTime.now();
    }


    public void decreaseQuantity(int amount) {

        if (this.quantity < amount) {
            throw new OutOfStockException(buildStockErrorMessage(amount));
        }

        this.quantity -= amount;
    }

    private String buildStockErrorMessage(int amount) {
        return String.format("상품 ID: %d, 요청 수량: %d → 현재 재고: %d (부족)", this.id, amount, this.quantity);
    }

}
