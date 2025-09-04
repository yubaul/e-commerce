package kr.baul.server.infrastructure.ouxbox;

import kr.baul.server.common.exception.EntityNotFoundException;
import kr.baul.server.domain.ouxbox.OutboxEvent;
import kr.baul.server.domain.ouxbox.OutboxEventReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxEventReaderImpl implements OutboxEventReader {

    private final OutboxEventRepository outboxEventRepository;

    @Override
    public OutboxEvent getOutboxEvent(String topic, String aggregateId) {
        return outboxEventRepository.findByTopicAndAggregateId(topic,aggregateId)
                .orElseThrow(() -> new EntityNotFoundException("해당 이벤트가 없습니다."));
    }

    @Override
    public OutboxEvent getPendingEvent(String topic, String aggregateId) {
        return outboxEventRepository.findByTopicAndAggregateIdAndStatus(
                topic,
                aggregateId,
                OutboxEvent.OutboxEventStatus.PENDING
        ).orElseThrow(() -> new EntityNotFoundException("해당 이벤트가 없습니다."));
    }

    @Override
    public List<OutboxEvent> getPendingEvents(int limit) {
        return outboxEventRepository.findByStatus(
                OutboxEvent.OutboxEventStatus.PENDING,
                PageRequest.of(0, limit)
        );
    }


}
