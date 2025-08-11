package kr.baul.server.domain.item;

public interface ItemStockReader {
    ItemStock getItemStockWithLock(Long itemIId);
}
