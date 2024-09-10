import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;

import java.util.Map;

public class OpenTelemetryServiceB {

    private static final Tracer tracer = GlobalOpenTelemetry.get().getTracer("service-b");
    private final RedisHelper redisHelper = new RedisHelper();
    private final RedisHelper.TextMapPropagator propagator = new RedisHelper.TextMapPropagator();

    public void firstMethod() {
        // Read the message from Redis
        Map<String, String> receivedMessage = redisHelper.receiveMessage("serviceBStream");

        // Extract trace context and create a child span
        Context extractedContext = W3CTraceContextPropagator.getInstance().extract(Context.current(), receivedMessage, propagator);
        Span span = tracer.spanBuilder("ServiceB.firstMethod")
                .setParent(extractedContext)
                .startSpan();
        
        try {
            span.setAttribute("service", "B");
            span.setAttribute("method", "firstMethod");

            System.out.println("Service B: First method running...");

            // Continue the operation or call another method
            secondMethod();

        } finally {
            span.end();
        }
    }

    public void secondMethod() {
        Span span = tracer.spanBuilder("ServiceB.secondMethod").startSpan();
        try {
            span.setAttribute("service", "B");
            span.setAttribute("method", "secondMethod");

            System.out.println("Service B: Second method running...");
        } finally {
            span.end();
        }
    }
}
