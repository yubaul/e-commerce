package kr.hhplus.be.server.item;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import kr.baul.server.application.item.ItemInfo;
import kr.baul.server.application.item.ItemService;
import kr.baul.server.common.exception.EntityNotFoundException;
import kr.baul.server.domain.item.Item;
import kr.baul.server.domain.item.ItemReader;
import kr.baul.server.domain.item.ItemStock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {

    @InjectMocks
    ItemService itemService;

    @Mock
    ItemReader itemReader;

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

        when(itemReader.getItem(itemId)).thenReturn(item);

        // when
        ItemInfo result = itemService.getItem(itemId);

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getId()).isEqualTo(itemId),
                () -> assertThat(result.getName()).isEqualTo("테스트 상품"),
                () -> assertThat(result.getPrice()).isEqualTo(85_000L)
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
}