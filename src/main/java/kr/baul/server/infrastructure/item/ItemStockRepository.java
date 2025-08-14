package kr.baul.server.infrastructure.item;

import kr.baul.server.domain.item.ItemStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemStockRepository extends JpaRepository<ItemStock, Long> {

    Optional<ItemStock> findByItemId(Long itemId);

}
