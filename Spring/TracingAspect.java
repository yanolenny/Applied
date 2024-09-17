package com.example.aspect;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TracingAspect {

    private final Tracer tracer;

    public TracingAspect() {
        this.tracer = GlobalOpenTelemetry.get().getTracer("tracing-aspect");
    }

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || execution(* com.example..*(..))")
    public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        SpanBuilder spanBuilder = tracer.spanBuilder(methodName);
        Span span = spanBuilder.startSpan();

        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            span.recordException(throwable);
            span.setStatus(StatusCode.ERROR, "Error occurred");
            throw throwable;
        } finally {
            span.end();
        }
    }
}
