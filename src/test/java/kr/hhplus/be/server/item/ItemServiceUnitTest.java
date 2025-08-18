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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {

    @InjectMocks
    ItemService itemService;

    @Mock
    ItemReader itemReader;


    @Mock
    ItemInfoMapper itemInfoMapper;

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
        List<ItemInfo.TopSelling> expected = List.of(mock(ItemInfo.TopSelling.class));
        when(itemReader.getTopSellingItems(any(), any(), anyInt()))
                .thenReturn(expected);

        // when
        List<ItemInfo.TopSelling> result = itemService.getTopSellingItems();

        // then
        assertThat(result).isEqualTo(expected);
        verify(itemReader).getTopSellingItems(any(), any(), eq(5));
    }
}