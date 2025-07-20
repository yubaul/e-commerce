package kr.hhplus.be.server.mock;

import kr.baul.server.ServerApplication;
import static kr.baul.server.mock.account.MockAccountApiController.MockAccountChargeRequest;

import static kr.baul.server.mock.account.MockAccountApiController.*;
import kr.baul.server.response.CommonResponse;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ServerApplication.class)
public class MockAccountApiControllerE2ETest  {

    @Autowired private TestRestTemplate restTemplate;

    private static final String BASE_URL = "/api/v1/mock/accounts";

    @Test
    void 잔액_조회_E2E_테스트_성공() {
        // given
        Long userId = 1L;
        String url = BASE_URL + "/" + userId;

        // when
        ResponseEntity<CommonResponse<MockAccount>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {}
                );


        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getResult()).isEqualTo(CommonResponse.Result.SUCCESS);
        assertThat(response.getBody().getData().balance()).isEqualTo(MockAccount.dummy().balance());

    }

    @Test
    void 잔액_충전_E2E_테스트_성공() {
        // given
        MockAccountChargeRequest request = new MockAccountChargeRequest();
        request.setUserId(1L);
        request.setAmount(10_000L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MockAccountChargeRequest> httpEntity = new HttpEntity<>(request, headers);

        // when
        ResponseEntity<CommonResponse<MockChargeBalance>> response = restTemplate.exchange(
                BASE_URL + "/charge",
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<>() {}
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getResult()).isEqualTo(CommonResponse.Result.SUCCESS);
        assertThat(response.getBody().getData().balance()).isEqualTo(MockChargeBalance.dummy().balance());
    }
}
