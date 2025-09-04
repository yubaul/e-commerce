package kr.baul.server.infrastructure.ouxbox;

import kr.baul.server.domain.ouxbox.OutboxEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    Optional<OutboxEvent> findByTopicAndAggregateIdAndStatus(String topic,
                                                             String aggregateId,
                                                             OutboxEvent.OutboxEventStatus status);

    Optional<OutboxEvent> findByTopicAndAggregateId(String topic, String aggregateId);

    List<OutboxEvent> findByStatus(OutboxEvent.OutboxEventStatus status, Pageable pageable);

}
