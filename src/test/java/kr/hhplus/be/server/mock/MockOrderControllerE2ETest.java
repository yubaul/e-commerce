package kr.hhplus.be.server.mock;

import kr.baul.server.ServerApplication;
import kr.baul.server.mock.order.MockOrderController.MockOrderRequest;
import kr.baul.server.mock.order.MockOrderController.MockOrderRequest.MockOrderItem;
import kr.baul.server.mock.order.MockOrderController.MockOrderResult;
import kr.baul.server.response.CommonResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ServerApplication.class)
public class MockOrderControllerE2ETest {

    @Autowired
    TestRestTemplate restTemplate;

    private static final String BASE_URL = "/api/v1/mock/orders";

    @Test
    void 주문_결제_성공() {
        // given
        MockOrderRequest request = new MockOrderRequest();
        request.setUserId(1L);

        MockOrderItem item1 = new MockOrderItem();
        item1.setItemId(100L);
        item1.setQuantity(2);

        request.setItems(List.of(item1));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MockOrderRequest> httpEntity = new HttpEntity<>(request, headers);

        // when
        ResponseEntity<CommonResponse<MockOrderResult>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<>() {}
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CommonResponse<MockOrderResult> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getResult()).isEqualTo(CommonResponse.Result.SUCCESS);
        assertThat(body.getData().orderId()).isEqualTo(MockOrderResult.dummy().orderId());
        assertThat(body.getData().status()).isEqualTo(MockOrderResult.dummy().status());
    }
}