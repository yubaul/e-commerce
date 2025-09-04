package kr.baul.server.infrastructure.ouxbox;

import kr.baul.server.domain.ouxbox.OutboxEvent;
import kr.baul.server.domain.ouxbox.OutboxEventReader;
import kr.baul.server.domain.ouxbox.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxRelayWorker {

    private final OutboxEventReader outboxEventReader;
    private final OutboxService outboxService;


    private final KafkaTemplate<String, Object> kafka;

    public void drainOnce(String topic, String eventId) {
        var event = outboxEventReader.getPendingEvent(topic, eventId);
        kafka.send(event.getTopic(), event.getAggregateId(), event.getPayload())
                .whenComplete((r, ex) -> {
                    if (ex == null) {
                        outboxService.markPublished(topic, eventId);
                    } else {
                        outboxService.markFailed(topic, eventId, ex.toString());
                    }
                });
    }

    /**
     * 전체 PENDING 이벤트를 주기적으로 재발행 (백업 안전망)
     */
    @Scheduled(fixedDelayString = "5000")
    public void replayPending() {
        List<OutboxEvent> batch = outboxEventReader.getPendingEvents(200);

        for (var e : batch) {
            kafka.send(e.getTopic(), e.getAggregateId(), e.getPayload())
                    .whenComplete((r, ex) -> {
                        if (ex == null) {
                            outboxService.markPublished(e.getTopic(), e.getAggregateId());
                        } else {
                            outboxService.markFailed(e.getTopic(), e.getAggregateId(), ex.toString());
                        }
                    });
        }
    }


}
