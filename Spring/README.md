# Spring Boot OpenTelemetry Tracing with AOP

This project demonstrates how to enable OpenTelemetry tracing in a Spring Boot application using Spring AOP without modifying business logic code directly.

## Setup

1. Add the OpenTelemetry dependencies in the `pom.xml` file.
2. Implement an `Aspect` to wrap method executions and send telemetry data to your OpenTelemetry backend.
3. Configure your `application.properties` to export traces to a collector.

### Dependencies

```xml
<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-api</artifactId>
    <version>1.25.0</version>
</dependency>
<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-sdk</artifactId>
    <version>1.25.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-otlp</artifactId>
    <version>1.25.0</version>
</dependency>
```

### Application Configuration

Configure the OpenTelemetry exporter in `application.properties`:

```properties
otel.exporter.otlp.endpoint=http://localhost:4317
otel.resource.attributes=service.name=my-spring-service
```

### Aspect for Tracing

Use Spring AOP to automatically wrap methods with OpenTelemetry spans:

```java
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
```

### Run the Application

Start the Spring Boot application, and traces will automatically be sent to your configured OpenTelemetry backend (e.g., Jaeger, Zipkin, NewRelic, etc.).
