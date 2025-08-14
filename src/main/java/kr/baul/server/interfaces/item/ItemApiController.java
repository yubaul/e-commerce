package kr.baul.server.interfaces.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.baul.server.application.item.ItemService;
import kr.baul.server.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Item API", description = "상품 API (단일 상품 / 인기 상품 조회)")
@RequestMapping("/api/v1/items")
@RestController
@RequiredArgsConstructor
public class ItemApiController {

    private final ItemService itemService;
    private final ItemDtoMapper itemDtoMapper;

    @Operation(summary = "단일 상품 조회 API", description = "itemId를 통해 단일 상품을 조회합니다.")
    @GetMapping("/{itemId}")
    public CommonResponse<ItemDto.ItemResponse> getItem(
            @Parameter(
                    name = "itemId",
                    description = "상품 ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long itemId
    ){
        var result = itemService.getItem(itemId);
        var response = itemDtoMapper.toResponse(result);
        return CommonResponse.success(response);
    }


    @Operation(summary = "인기 상품 조회 API", description = "최근 3일 동안 가장 많이 팔린 상위 5개 상품을 조회합니다.")
    @GetMapping("/top-selling")
    public CommonResponse<ItemDto.TopSellingResponse> getTopSellingItems(){
        var result = itemService.getTopSellingItems();
        var response = itemDtoMapper.toTopSellingResponse(result);
        return CommonResponse.success(response);
    }

}
