package kr.baul.server.mock.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.baul.server.common.response.CommonResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "MockItem", description = "상품 Mock API (단일 상품 / 인기 상품 조회)")
@RequestMapping("/api/v1/mock/items")
@RestController
public class MockItemController {
    @Operation(summary = "단일 상품 조회 API", description = "itemId를 통해 단일 상품을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "상품 조회 성공")
    @GetMapping("/{itemId}")
    public CommonResponse getItem(
            @Parameter(
                    name = "itemId",
                    description = "상품 ID",
                    required = true,
                    example = "10"
            )
            @PathVariable Long itemId
    ){
        var result = MockItem.dummy();
        return CommonResponse.success(result);
    }

    public record MockItem(long id,
                    String name,
                    long price,
                    long quantity
    ){
        public static MockItem dummy(){
            return new MockItem(10L, "면도 젤", 23_000L, 20L);
        }
    }
    @Operation(summary = "인기 상품 조회 API", description = "최근 3일 동안 가장 많이 팔린 상위 5개 상품을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "인기 상품 조회 성공")
    @GetMapping("/top-selling")
    public CommonResponse getTopSellingItems(){
        var result = MockTopSellingItemList.dummy();
        return CommonResponse.success(result);
    }

    public record MockTopSellingItem(long ranking,
                              long id, String name,
                              long price,
                              long totalSold
    ){

    }

    public record MockTopSellingItemList(List<MockTopSellingItem> itemList){
        public static MockTopSellingItemList dummy(){
            List<MockTopSellingItem> itemList = new ArrayList<>();
            var dummy1 = new MockTopSellingItem(1L, 100L, "제습기", 223_000L, 60L);
            var dummy2 = new MockTopSellingItem(2L, 101L, "에어컨", 723_000L, 50L);
            var dummy3 = new MockTopSellingItem(3L, 102L, "슬리퍼", 3_000L, 20L);
            var dummy4 = new MockTopSellingItem(4L, 103L, "에어팟", 323_000L, 5L);
            var dummy5 = new MockTopSellingItem(5L, 104L, "맥북", 1_123_000L, 3L);

            itemList.add(dummy1);
            itemList.add(dummy2);
            itemList.add(dummy3);
            itemList.add(dummy4);
            itemList.add(dummy5);

            return new MockTopSellingItemList(itemList);
        }
    }
}
