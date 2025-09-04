package kr.baul.server.domain.ouxbox;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "outbox_event")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String topic;

    @Column(name = "aggregate_id")
    private String aggregateId;

    @Column(name = "type")
    private String type;

    @Lob
    @Column(name = "payload")
    private String payload;

    @Lob
    @Column(name = "headers")
    private String headers;

    @Column(name = "occurred_at")
    private LocalDateTime occurredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OutboxEventStatus status;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Lob
    @Column(name = "last_error")
    private String lastError;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;


    @Getter
    @RequiredArgsConstructor
    public enum OutboxEventStatus{
        PENDING("전송 대기"),
        PUBLISHED("전송 완료"),
        COMPLETED("사용 완료"),
        FAILED("실패");

        private final String description;
    }

    public void markPublished(){
        this.status = OutboxEventStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public void markFailed(String ex){
        this.retryCount += 1;
        this.lastError = truncate(ex, 4000);
    }

    private String truncate(String s, int max) {
        return (s == null || s.length() <= max) ? s : s.substring(0, max);
    }

    public void markCompleted(){
        this.status = OutboxEventStatus.COMPLETED;
    }

}
