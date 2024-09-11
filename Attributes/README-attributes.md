
# OpenTelemetry Java Example - Attributes

This example demonstrates how to use OpenTelemetry in a Java application to create parent-child spans with unique attributes across multiple classes. The spans are sent to an OpenTelemetry backend (e.g., Grafana Tempo) for visualization.

## Prerequisites

- Java 8 or higher
- Gradle (to build and run the example)
- Grafana Tempo (or any OpenTelemetry-compatible tracing backend)

## Example Overview

The application consists of two services (`OpenTelemetryServiceA` and `OpenTelemetryServiceB`). Each service has two methods. The methods in one service call the methods in the other service, creating parent-child spans.

### Trace Flow:

1. **Service A**: `firstMethod()` → Calls → **Service B**: `firstMethod()`
2. **Service B**: `firstMethod()` → Calls → **Service A**: `secondMethod()`
3. **Service A**: `secondMethod()` → Calls → **Service B**: `secondMethod()`

### Key Features:
- Each service has a unique service name (`service-a` for `OpenTelemetryServiceA`, and `service-b` for `OpenTelemetryServiceB`).
- Each method adds unique span attributes such as `service`, `method`, and `operation` to help identify spans in the trace.
- Parent-child relationships are automatically created when one method calls another.

### Running the Attributes Example

1. **Navigate to the project directory**:
   ```bash
   cd /path/to/your/project
   ```

2. **Run the application using Gradle**:
   ```bash
   ./gradlew run
   ```

   This will start the application, and the spans will be sent to your OpenTelemetry backend (e.g., Grafana Tempo).

3. **View the trace**:
   Open Grafana Tempo (or any other OpenTelemetry-compatible tool) to view the traces. You should see the spans with unique attributes and parent-child relationships across the two services.

### Project Structure

- **`MainApplication.java`**: The entry point for the example, which initializes OpenTelemetry and starts the services.
- **`OpenTelemetryServiceA.java`**: Contains methods that create spans and communicate with `OpenTelemetryServiceB`.
- **`OpenTelemetryServiceB.java`**: Contains methods that create spans and receive communication from `OpenTelemetryServiceA`.

### Dependencies

The project uses the following dependencies:

- **OpenTelemetry API** (`io.opentelemetry:opentelemetry-api`)
- **OpenTelemetry SDK** (`io.opentelemetry:opentelemetry-sdk`)
- **OTLP Exporter** (`io.opentelemetry:opentelemetry-exporter-otlp`)
- **gRPC** (`io.grpc:grpc-netty-shaded`)

These dependencies are managed through Gradle.

### Build and Run

To build and run the example:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-repo.git
   cd your-repo
   ```

2. **Build the project**:
   ```bash
   ./gradlew build
   ```

3. **Run the project**:
   ```bash
   ./gradlew run
   ```

   This will run the `MainApplication` which triggers the `Attributes` example.

### License

This project is licensed under the MIT License.
