import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

public class OpenTelemetryServiceA {

    private static final Tracer tracer = GlobalOpenTelemetry.get().getTracer("service-a");

    private final OpenTelemetryServiceB serviceB;

    // Constructor to inject the other service
    public OpenTelemetryServiceA(OpenTelemetryServiceB serviceB) {
        this.serviceB = serviceB;
    }

    // First method of Service A invokes first method of Service B
    public void firstMethod() {
        Span span = tracer.spanBuilder("ServiceA.firstMethod").startSpan();
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("service", "A");
            span.setAttribute("method", "firstMethod");
            span.setAttribute("operation", "complexOperationA1");

            System.out.println("Service A: First method running...");

            // Invoke first method of Service B
            serviceB.firstMethod();
        } finally {
            span.end();
        }
    }

    // Second method of Service A invokes second method of Service B
    public void secondMethod() {
        Span span = tracer.spanBuilder("ServiceA.secondMethod").startSpan();
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("service", "A");
            span.setAttribute("method", "secondMethod");
            span.setAttribute("operation", "complexOperationA2");

            System.out.println("Service A: Second method running...");

            // Invoke second method of Service B
            serviceB.secondMethod();
        } finally {
            span.end();
        }
    }
}
