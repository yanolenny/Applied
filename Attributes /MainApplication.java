import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;

import java.time.Duration;

public class MainApplication {

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

        // Create the service instances (passing each other as dependencies)
        OpenTelemetryServiceA serviceA = new OpenTelemetryServiceA(null); // temp null
        OpenTelemetryServiceB serviceB = new OpenTelemetryServiceB(serviceA);
        // Now pass serviceB back to serviceA
        serviceA = new OpenTelemetryServiceA(serviceB);

        // Service A operations (this will trigger the chain of method calls)
        serviceA.firstMethod();

        // Shut down the TracerProvider to flush and clean up resources
        tracerProvider.close();
    }
}
