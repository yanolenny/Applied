import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.Context;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;

import java.util.HashMap;
import java.util.Map;

public class OpenTelemetryServiceA {

    private static final Tracer tracer = GlobalOpenTelemetry.get().getTracer("service-a");
    private final RedisHelper redisHelper = new RedisHelper();
    private final RedisHelper.TextMapPropagator propagator = new RedisHelper.TextMapPropagator();

    public void firstMethod() {
        Span span = tracer.spanBuilder("ServiceA.firstMethod").startSpan();
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("service", "A");
            span.setAttribute("method", "firstMethod");

            System.out.println("Service A: First method running...");

            // Propagate context and send message via Redis
            Map<String, String> traceContext = new HashMap<>();
            W3CTraceContextPropagator.getInstance().inject(Context.current(), traceContext, propagator);
            redisHelper.sendMessageWithContext("serviceBStream", "Trigger secondMethod in ServiceB", traceContext);

        } finally {
            span.end();
        }
    }
}
