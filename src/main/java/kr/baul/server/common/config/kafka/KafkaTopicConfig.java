package kr.baul.server.common.config.kafka;

import kr.baul.server.common.constants.KafkaConstant;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderPaymentTopic() {
        return TopicBuilder.name(KafkaConstant.ORDER_PAYMENT)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderPaymentDlq() {
        return TopicBuilder.name(KafkaConstant.ORDER_PAYMENT + ".DLQ")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic couponIssueTopic() {
        return TopicBuilder.name(KafkaConstant.COUPON_ISSUE)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic couponIssueDlq() {
        return TopicBuilder.name(KafkaConstant.COUPON_ISSUE + ".DLQ")
                .partitions(3)
                .replicas(1)
                .build();
    }

}
