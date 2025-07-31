package kr.baul.server.domain.item;

import jakarta.persistence.*;
import kr.baul.server.common.jpa.AbstractEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor
@Table(name = "items")
public class Item extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Long price;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "item_id")
    private ItemStock itemStock;


    @Builder
    public Item(
            Long id,
            String name,
            Long price,
            ItemStock itemStock
    ){
        this.id = id;
        this.name = name;
        this.price = price;
        this.itemStock = itemStock;
    }


    public void decreaseQuantity(int amount) {
        this.itemStock.decrease(amount);
    }




}
