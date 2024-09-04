import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;

import java.time.Duration;

public class OpenTelemetryToTempoExample {

    // Obtain the tracer from the GlobalOpenTelemetry
    private static final Tracer tracer = GlobalOpenTelemetry.get().getTracer("exampleTracer");

    public static void main(String[] args) {
        // Configure the OTLP gRPC exporter to send data to Tempo
        SpanExporter otlpGrpcSpanExporter = OtlpGrpcSpanExporter.builder()
            .setEndpoint("http://localhost:4317") // Update with your Tempo endpoint
            .setTimeout(Duration.ofSeconds(30))
            .build();

        // Create a TracerProvider and add a BatchSpanProcessor to export spans
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(BatchSpanProcessor.builder(otlpGrpcSpanExporter).build())
            .build();

        // Initialize OpenTelemetry SDK with the created TracerProvider
        OpenTelemetrySdk openTelemetry = OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .buildAndRegisterGlobal();

        // Start a parent span and call the first method
        Span parentSpan = tracer.spanBuilder("parentSpan").startSpan();
        try (Scope scope = parentSpan.makeCurrent()) {
            firstMethod();
        } finally {
            // End the parent span
            parentSpan.end();
        }

        // Shut down the TracerProvider to flush and clean up resources
        tracerProvider.close();
    }

    private static void firstMethod() {
        // Start a new span for this method
        Span firstMethodSpan = tracer.spanBuilder("firstMethodSpan").startSpan();
        try (Scope scope = firstMethodSpan.makeCurrent()) {
            // Do some work in this method
            System.out.println("In the first method, doing work...");

            // Call the second method, which will create a child span
            secondMethod();
        } finally {
            // End the span for this method
            firstMethodSpan.end();
        }
    }

    private static void secondMethod() {
        // Start a new child span for this method
        Span secondMethodSpan = tracer.spanBuilder("secondMethodSpan")
            .setParent(Span.current())  // Set the current span as parent
            .startSpan();
        try (Scope scope = secondMethodSpan.makeCurrent()) {
            // Do some work in this method
            System.out.println("In the second method, doing work...");
        } finally {
            // End the span for this method
            secondMethodSpan.end();
        }
    }
}
