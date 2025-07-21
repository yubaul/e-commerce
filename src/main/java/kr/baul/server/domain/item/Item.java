package kr.baul.server.domain.item;

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

}
