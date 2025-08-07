package kr.baul.server.infrastructure.item;

import jakarta.persistence.LockModeType;
import kr.baul.server.domain.item.ItemStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface ItemStockRepository extends JpaRepository<ItemStock, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ItemStock> findForUpdateByItemId(Long itemId);

}
