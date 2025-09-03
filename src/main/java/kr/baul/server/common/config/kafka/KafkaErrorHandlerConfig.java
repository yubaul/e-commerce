package kr.baul.server.common.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaErrorHandlerConfig {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaErrorHandlerConfig(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        // DeadLetterPublishingRecoverer: 실패 메시지를 단순히 .DLQ 토픽으로 라우팅
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (ConsumerRecord<?, ?> record, Exception ex) -> {
                    String dltTopic = record.topic() + ".DLQ";

                    log.error("[DLT 전송] 원본 토픽={}, key={}, value={}, 이유={}",
                            record.topic(), record.key(), record.value(), ex.toString());

                    return new TopicPartition(dltTopic, record.partition());
                }
        );

        // 1초 간격으로 3번 재시도 후 recoverer 실행 → .DLQ 전송
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
    }

}
