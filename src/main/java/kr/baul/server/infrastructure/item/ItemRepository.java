package kr.baul.server.infrastructure.item;

import kr.baul.server.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ItemRepository extends JpaRepository<Item, Long> {
}
