package kr.hhplus.be.server.mock;

import kr.baul.server.ServerApplication;
import kr.baul.server.mock.coupon.MockCouponController.MockCoupon;
import kr.baul.server.mock.coupon.MockCouponController.MockCouponList;
import kr.baul.server.mock.coupon.MockCouponController.MockIssueCouponRequest;
import kr.baul.server.common.response.CommonResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ServerApplication.class)
public class MockCouponControllerE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE_URL = "/api/v1/mock/coupon";

    @Test
    void 선착순_쿠폰_발급_E2E_성공() {
        // given
        MockIssueCouponRequest request = new MockIssueCouponRequest();
        request.setUserId(1L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MockIssueCouponRequest> httpEntity = new HttpEntity<>(request, headers);

        // when
        ResponseEntity<CommonResponse<MockCoupon>> response = restTemplate.exchange(
                BASE_URL + "/issue",
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<>() {}
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getResult()).isEqualTo(CommonResponse.Result.SUCCESS);
        assertThat(response.getBody().getData().name()).isEqualTo(MockCoupon.dummy().name());
    }

    @Test
    void 보유_쿠폰_목록_조회_E2E_성공() {
        // given
        Long userId = 1L;
        String url = BASE_URL + "/" + userId;

        // when
        ResponseEntity<CommonResponse<MockCouponList>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getResult()).isEqualTo(CommonResponse.Result.SUCCESS);
        assertThat(response.getBody().getData().coupons().get(0).name())
                .isEqualTo(MockCouponList.dummy().coupons().get(0).name());
    }
}