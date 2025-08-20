package kr.hhplus.be.server.item;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import kr.baul.server.domain.item.iteminfo.ItemInfo;
import kr.baul.server.application.item.ItemService;
import kr.baul.server.common.exception.EntityNotFoundException;
import kr.baul.server.domain.item.Item;
import kr.baul.server.domain.item.ItemReader;
import kr.baul.server.domain.item.ItemStock;
import kr.baul.server.domain.item.iteminfo.ItemInfoMapper;
import kr.baul.server.domain.item.itemrank.ItemRankingMerger;
import kr.baul.server.domain.item.itemrank.ItemRankingReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {

    @InjectMocks
    ItemService itemService;

    @Mock
    ItemReader itemReader;


    @Mock
    ItemInfoMapper itemInfoMapper;

    @Mock
    ItemRankingReader itemRankingReader;

    @Mock
    ItemRankingMerger itemRankingMerger;

    @Test
    void 상품_조회_예외_없음() {
        // given
        Long itemId = 1L;
        ItemStock itemStock = ItemStock.builder()
                .itemId(itemId)
                .quantity(50)
                .build();
        Item item = Item.builder()
                .id(itemId)
                .name("테스트 상품")
                .price(85_000L)
                .itemStock(itemStock)
                .build();
        ItemInfo.Item mapped = new ItemInfo.Item(itemId, "테스트 상품", 85_000L, 50);

        when(itemReader.getItem(itemId)).thenReturn(item);
        when(itemInfoMapper.of(item)).thenReturn(mapped);

        // when
        ItemInfo.Item result = itemService.getItem(itemId);

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.id()).isEqualTo(itemId),
                () -> assertThat(result.name()).isEqualTo("테스트 상품"),
                () -> assertThat(result.price()).isEqualTo(85_000L)
        );
    }

    @Test
    void 상품_조회_실패_존재하지_않는_상품_예외() {
        // given
        Long itemId = 999L;
        when(itemReader.getItem(itemId))
                .thenThrow(new EntityNotFoundException("해당 ID의 상품이 존재하지 않습니다."));

        // when & then
        assertThatThrownBy(() -> itemService.getItem(itemId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 ID의 상품이 존재하지 않습니다.");
    }

    @Test
    void 인기상품_조회_정상_조회() {
        // given
        Long itemId = 2300L;
        String itemName = "Item A";

        var rankedTopSelling = ItemInfo.TopSelling.builder()
                .itemId(itemId)
                .salesVolume(50)
                .build();
        var ranked = List.of(rankedTopSelling);

        var itemEntity = Item.builder()
                .id(itemId)
                .name(itemName)
                .build();

        var expectedTopSelling = ItemInfo.TopSelling.builder()
                .itemId(itemId)
                .itemName(itemName)
                .salesVolume(50)
                .build();
        var expected = List.of(expectedTopSelling);

        when(itemRankingReader.getTop5ItemsLast3Days()).thenReturn(ranked);
        when(itemReader.getItems(List.of(itemId))).thenReturn(List.of(itemEntity));
        when(itemRankingMerger.merge(ranked, List.of(itemEntity))).thenReturn(expected);

        // when
        var result = itemService.getTopSellingItems();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItemId()).isEqualTo(itemId);
        assertThat(result.get(0).getItemName()).isEqualTo(itemName);
    }
}