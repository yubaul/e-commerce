package kr.baul.server.infrastructure.ouxbox;

import kr.baul.server.domain.ouxbox.OutboxEvent;
import kr.baul.server.domain.ouxbox.OutboxEventStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventStoreImpl implements OutboxEventStore {

    private final OutboxEventRepository outboxEventRepository;

    @Override
    public OutboxEvent store(OutboxEvent outboxEvent) {
        return outboxEventRepository.save(outboxEvent);
    }
}
