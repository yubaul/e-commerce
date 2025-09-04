package kr.baul.server.common.config.lock;

import kr.baul.server.common.exception.LockAcquisitionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonLockAop {

    private static final String LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final LockTransaction lockTransaction;

    @Around("@annotation(kr.baul.server.common.config.lock.CommonLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CommonLock commonLock = method.getAnnotation(CommonLock.class);

        String dynamicKey = commonLock.key();
        String lockIdPart = buildIdPart(joinPoint, method, commonLock);

        String lockKey = LOCK_PREFIX + dynamicKey + (lockIdPart.isEmpty() ? "" : ":" + lockIdPart);
        RLock rLock = redissonClient.getLock(lockKey);
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

    private String buildIdPart(ProceedingJoinPoint pjp, Method method, CommonLock ann) {
        var names = new DefaultParameterNameDiscoverer();
        var ctx = new MethodBasedEvaluationContext(pjp.getTarget(), method, pjp.getArgs(), names);
        var parser = new SpelExpressionParser();


        String[] ids = ann.ids();
        java.util.List<String> parts = new java.util.ArrayList<>();

        if (ids != null && ids.length > 0) {
            for (String expr : ids) {
                parts.add(evalOrLiteral(parser, ctx, expr));
            }
        } else {
            String expr = ann.id();
            if (expr != null && !expr.isBlank()) {
                parts.add(evalOrLiteral(parser, ctx, expr));
            }
        }

        parts.removeIf(s -> s == null || s.isBlank());
        parts.replaceAll(String::trim);


        return String.join(":", parts);
    }

    private String evalOrLiteral(ExpressionParser parser,
                                 EvaluationContext ctx,
                                 String expr) {
        if (expr == null) return null;
        String s = expr.trim();
        boolean looksLikeSpEL = s.startsWith("#") || s.contains(".") || s.contains("[") || s.contains("(");
        if (!looksLikeSpEL) return s;
        Object val = parser.parseExpression(s).getValue(ctx);
        return val == null ? null : String.valueOf(val);
    }


    private void safeUnlock(RLock lock) {
        try {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        } catch (IllegalMonitorStateException ignored) {

        }
    }

}
