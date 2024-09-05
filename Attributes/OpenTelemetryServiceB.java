import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

public class OpenTelemetryServiceB {

    private static final Tracer tracer = GlobalOpenTelemetry.get().getTracer("service-b");

    private final OpenTelemetryServiceA serviceA;

    // Constructor to inject the other service
    public OpenTelemetryServiceB(OpenTelemetryServiceA serviceA) {
        this.serviceA = serviceA;
    }

    // First method of Service B invokes second method of Service A
    public void firstMethod() {
        Span span = tracer.spanBuilder("ServiceB.firstMethod").startSpan();
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("service", "B");
            span.setAttribute("method", "firstMethod");
            span.setAttribute("operation", "complexOperationB1");

            System.out.println("Service B: First method running...");

            // Invoke second method of Service A
            serviceA.secondMethod();
        } finally {
            span.end();
        }
    }

    // Second method of Service B (no further invocations)
    public void secondMethod() {
        Span span = tracer.spanBuilder("ServiceB.secondMethod").startSpan();
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("service", "B");
            span.setAttribute("method", "secondMethod");
            span.setAttribute("operation", "complexOperationB2");

            System.out.println("Service B: Second method running...");
        } finally {
            span.end();
        }
    }
}
