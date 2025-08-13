package kr.baul.server.common.config;

import kr.baul.server.common.exception.LockAcquisitionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonLockAop {

    private static final String LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final LockTransaction lockTransaction;

    @Around("@annotation(kr.baul.server.common.config.CommonLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CommonLock commonLock = method.getAnnotation(CommonLock.class);

        String dynamicKey = commonLock.key();
        String dynamicId  = evalSpEL(joinPoint, method, commonLock.id());
        String key = LOCK_PREFIX + dynamicKey + dynamicId;
        RLock rLock = redissonClient.getLock(key);
        boolean locked = false;

        try {
            locked = rLock.tryLock(commonLock.waitTime(), commonLock.leaseTime(), commonLock.timeUnit());  // (2)
            if (!locked) {
                throw new LockAcquisitionException();
            }

            return lockTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            if (locked) safeUnlock(rLock);
        }
    }

    private String evalSpEL(ProceedingJoinPoint pjp, Method method, String expr) {
        var parser = new org.springframework.expression.spel.standard.SpelExpressionParser();
        var nameDiscoverer = new org.springframework.core.DefaultParameterNameDiscoverer();

        var context = new org.springframework.context.expression.MethodBasedEvaluationContext(
                pjp.getTarget(), method, pjp.getArgs(), nameDiscoverer);

        Object val = parser.parseExpression(expr).getValue(context);
        return val == null ? null : String.valueOf(val);
    }

    private void safeUnlock(RLock lock) {
        try {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        } catch (IllegalMonitorStateException ignored) {

        }
    }

}
