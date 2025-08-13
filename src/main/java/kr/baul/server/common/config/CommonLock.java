package kr.baul.server.common.config;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommonLock {

    /**
     * 락의 이름
     */
    String key();

    /**
     * 접근 테이블 ID
     */
    String id();

    /**
     * 락의 시간 단위
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 락을 기다리는 시간 (default - 800ms)
     * 락 획득을 위해 waitTime 만큼 대기
     */
    long waitTime() default 800L;

    /**
     * 락 임대 시간 (default - 500ms)
     * 락을 획득한 이후 leaseTime 이 지나면 락을 해제
     */
    long leaseTime() default 500L;

}
