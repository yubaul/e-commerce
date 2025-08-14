package kr.hhplus.be.server.item;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import kr.baul.server.domain.item.iteminfo.ItemInfo;
import kr.baul.server.application.item.ItemService;
import kr.baul.server.common.exception.EntityNotFoundException;
import kr.hhplus.be.server.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest(
        classes = kr.baul.server.ServerApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
public class ItemServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ItemService itemService;

    @Test
    void 상품_조회_성공_테스트() {
        // given
        Long itemId = 1L;

        // when
        ItemInfo.Item result = itemService.getItem(itemId);

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.id()).isEqualTo(itemId)
        );
    }

    @Test
    void 상품_조회_실패_존재하지_않는_상품_예외() {
        // given
        Long invalidItemId = 999L;

        // when & then
        assertThatThrownBy(() -> itemService.getItem(invalidItemId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 ID의 상품이 존재하지 않습니다.");
    }

    @Test
    void 인기상품_조회_검증() {
        // when
        List<ItemInfo.TopSelling> result = itemService.getTopSellingItems();

        // then
        assertThat(result).isNotEmpty();
        ItemInfo.TopSelling first = result.get(0);

        assertAll(
                () -> assertThat(first.getItemId()).isEqualTo(2004L),
                () -> assertThat(first.getItemName()).isEqualTo("Item E"),
                () -> assertThat(first.getSalesVolume()).isEqualTo(6)
        );
    }
}
