package kr.baul.server.domain.item;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.baul.server.common.exception.OutOfStockException;
import kr.baul.server.common.jpa.AbstractEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table( name = "item_stock")
public class ItemStock extends AbstractEntity {

    @Id
    @Column(name = "item_id")
    Long itemId;

    private int quantity;

    @Builder
    public ItemStock(Long itemId, int quantity){
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public void decrease(int amount) {
        if (this.quantity < amount) {
            throw new OutOfStockException(buildStockErrorMessage(amount));
        }
        this.quantity -= amount;
    }

    public void increase(int amount) {
        this.quantity += amount;
    }

    private String buildStockErrorMessage(int amount) {
        return String.format("상품 ID: %d, 요청 수량: %d → 현재 재고: %d (부족)", this.itemId, amount, this.quantity);
    }


}
