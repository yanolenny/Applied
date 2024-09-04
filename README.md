# Applied Materials - Open Observability

Project Structure

	•	build.gradle: Contains the necessary dependencies for OpenTelemetry, including the OTLP exporter for sending trace data to Tempo.
	•	OpenTelemetryToTempoExample.java: The main class that demonstrates how to create traces and spans using OpenTelemetry.

Dependencies

The following dependencies are used in this project:

	•	OpenTelemetry API (io.opentelemetry:opentelemetry-api): The core API for creating and managing telemetry data like traces and spans.
	•	OpenTelemetry SDK (io.opentelemetry:opentelemetry-sdk): The SDK implementation for configuring OpenTelemetry.
	•	OTLP Exporter (io.opentelemetry:opentelemetry-exporter-otlp): Used to export trace data to Tempo via gRPC.
	•	gRPC (io.grpc:grpc-netty-shaded): Required for sending trace data to Tempo over gRPC.

Code Explanation

Main Components

1.	OTLP Exporter Configuration

In the main method, the OTLP exporter is configured to send trace data to Tempo using gRPC:

```java
SpanExporter otlpGrpcSpanExporter = OtlpGrpcSpanExporter.builder()
    .setEndpoint("http://localhost:4317")
    .setTimeout(Duration.ofSeconds(0))
    .build();
```

• setEndpoint("http://localhost:4317"): The endpoint where Tempo is running (usually localhost:4317 for local instances).
• setTimeout(Duration.ofSeconds(0)): Timeout setting for sending the trace data.

2.	OpenTelemetry SDK Setup

The OpenTelemetry SDK is initialized with the configured OTLP exporter and a batch span processor:

```java
SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
    .addSpanProcessor(BatchSpanProcessor.builder(otlpGrpcSpanExporter).build())
    .build();

OpenTelemetrySdk openTelemetry = OpenTelemetrySdk.builder()
    .setTracerProvider(tracerProvider)
    .buildAndRegisterGlobal();
```

•	BatchSpanProcessor: Batches and sends spans asynchronously to improve performance.

3.	Parent and Child Spans
   
The application creates two methods (firstMethod() and secondMethod()) to demonstrate how parent and child spans are used in a trace hierarchy:
	•	firstMethod(): Creates a span named firstMethodSpan and calls secondMethod(), which creates a child span.
	•	secondMethod(): Creates a child span named secondMethodSpan, using the span from firstMethod() as its parent.
Here’s how the methods are structured:

```java
Span parentSpan = tracer.spanBuilder("parentSpan").startSpan();
try (Scope scope = parentSpan.makeCurrent()) {
    firstMethod();  // Calls the method that creates a child span
} finally {
    parentSpan.end();
}
```

4.	Tracing in Methods
	•	First Method (firstMethod()): Starts a span and performs some operation.
	•	Second Method (secondMethod()): Starts a child span under the span from the first method, simulating a sub-operation.
These spans are connected through OpenTelemetry, which tracks the relationship between them.

Example Trace Flow

	1.	Parent Span (parentSpan): Represents the overall operation.
	2.	Child Span (firstMethodSpan): Represents the operation in firstMethod().
	3.	Grandchild Span (secondMethodSpan): Represents the sub-operation in secondMethod().

These spans create a parent-child relationship, which can be viewed in Tempo to observe how the operations relate to each other in a distributed trace.
