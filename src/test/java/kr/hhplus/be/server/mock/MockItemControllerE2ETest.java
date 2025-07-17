package kr.hhplus.be.server.mock;

import kr.baul.server.ServerApplication;
import kr.baul.server.mock.item.MockItemController.MockItem;
import kr.baul.server.mock.item.MockItemController.MockTopSellingItemList;
import kr.baul.server.response.CommonResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ServerApplication.class)
public class MockItemControllerE2ETest {

    @Autowired TestRestTemplate restTemplate;

    private static final String BASE_URL = "/api/v1/mock/items";

    @Test
    void 단일_상품_조회_성공() {
        // given
        Long itemId = 10L;
        String url = BASE_URL + "/" + itemId;

        // when
        ResponseEntity<CommonResponse<MockItem>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getResult()).isEqualTo(CommonResponse.Result.SUCCESS);
        assertThat(response.getBody().getData().name()).isEqualTo(MockItem.dummy().name());
    }

    @Test
    void 인기_상품_조회_성공() {
        // given
        String url = BASE_URL + "/top-selling";

        // when
        ResponseEntity<CommonResponse<MockTopSellingItemList>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getResult()).isEqualTo(CommonResponse.Result.SUCCESS);
        assertThat(response.getBody().getData().itemList()).hasSize(5);
        assertThat(response.getBody().getData().itemList().get(0).name())
                .isEqualTo(response.getBody().getData().itemList().get(0).name());
    }
}