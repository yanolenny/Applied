
# OpenTelemetry Java Example with Multithreading

This repository demonstrates how to instrument a multithreaded Java application with OpenTelemetry, ensuring that the trace context is correctly propagated across different threads.

## Prerequisites

- Java 8 or higher
- Gradle (to build and run the example)
- Grafana Tempo (or any OpenTelemetry-compatible tracing backend)

## Example Overview

The application simulates multithreading using `ExecutorService` and demonstrates how to create parent-child spans where tasks are run in different threads. OpenTelemetry trace context is propagated across threads to maintain the correct trace structure.

### Trace Flow:

1. **Main Thread**: Starts a parent span (`MainThreadSpan`).
2. **Worker Threads**: Each worker thread (Task-1, Task-2, Task-3) starts a child span that is part of the parent span.

### Key Features:

- Proper propagation of OpenTelemetry trace context across thread boundaries.
- Each worker thread creates a child span that links back to the parent span in the main thread.

### Running the Example

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
   Open Grafana Tempo (or any other OpenTelemetry-compatible tool) to view the traces. You should see a parent span (`MainThreadSpan`) with child spans for each worker task.

### Project Structure

- **`MultiThreadedOpenTelemetryExample.java`**: The main class that demonstrates the use of OpenTelemetry in a multithreaded environment.

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

   This will run the `MultiThreadedOpenTelemetryExample` demonstrating OpenTelemetry in a multithreaded Java environment.

### License

This project is licensed under the MIT License.
