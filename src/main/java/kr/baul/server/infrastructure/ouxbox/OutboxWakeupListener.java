package kr.baul.server.infrastructure.ouxbox;

import kr.baul.server.domain.ouxbox.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OutboxWakeupListener {

    private final OutboxRelayWorker worker;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommitted(OutboxEvent e) {
        worker.drainOnce(e.getTopic(), e.getAggregateId());
    }

}
