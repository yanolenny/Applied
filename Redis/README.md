
# OpenTelemetry Java Examples

This repository contains two examples demonstrating the use of OpenTelemetry with Java. The examples include:

1. **Attributes Example**: Demonstrates how to create parent-child spans with different attributes in multiple classes.
2. **Redis Streams with Trace Context Propagation**: Shows how to pass the OpenTelemetry trace context through Redis Streams, ensuring that traces are not broken when methods communicate asynchronously via Redis.

## Prerequisites

- Java 8 or higher
- Gradle (to build and run the examples)
- Redis (for the Redis Streams example)
- Grafana Tempo (or any OpenTelemetry-compatible tracing backend)

## Examples

### 1. Attributes Example

In this example, we demonstrate how to create spans in two classes (`OpenTelemetryServiceA` and `OpenTelemetryServiceB`) and propagate trace context when methods in one class invoke methods in the other.

#### Trace Flow:

1. **Service A**: `firstMethod()` → Calls → **Service B**: `firstMethod()`
2. **Service B**: `firstMethod()` → Calls → **Service A**: `secondMethod()`
3. **Service A**: `secondMethod()` → Calls → **Service B**: `secondMethod()`

#### Key Features:
- Different service names for each class (`service-a` and `service-b`).
- Each method adds unique span attributes (`service`, `method`, `operation`).
- Parent-child span relationships are automatically maintained when one method calls another.

#### Running the Attributes Example:

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

### 2. Redis Streams with Trace Context Propagation Example

This example demonstrates how to propagate trace context when methods communicate asynchronously using Redis Streams. The trace context is passed as part of the message in Redis to ensure that the trace is not broken.

#### Trace Flow:

1. **Service A**: `firstMethod()` sends a message with the current trace context to Redis.
2. **Service B**: Reads the message from Redis, extracts the trace context, and continues the trace by invoking its methods (`firstMethod()` and `secondMethod()`).

#### Key Features:
- Uses Redis Streams for asynchronous communication.
- Propagates OpenTelemetry trace context via Redis to ensure that traces are not broken.
- Demonstrates how to inject and extract trace context using OpenTelemetry’s W3C Trace Context Propagator.

#### Running the Redis Streams Example:

1. **Start Redis**:
   Make sure Redis is running locally on port `6379`. You can start Redis using Docker if it's not installed locally:
   ```bash
   docker run -d -p 6379:6379 redis
   ```

2. **Navigate to the project directory**:
   ```bash
   cd /path/to/your/project
   ```

3. **Run the application using Gradle**:
   ```bash
   ./gradlew run
   ```

   This will start the application, where:
   - **Service A** sends a message with trace context to Redis.
   - **Service B** reads the message, extracts the trace context, and continues the trace.

4. **View the trace**:
   Open Grafana Tempo (or any other OpenTelemetry-compatible tool) to view the traces. You will see the full trace, including spans created by both services, even though they communicated asynchronously via Redis.

### Project Structure

- **`MainApplication.java`**: The entry point for the examples, which initializes OpenTelemetry and starts the services.
- **`OpenTelemetryServiceA.java`**: Contains methods that create spans and communicate with `OpenTelemetryServiceB`.
- **`OpenTelemetryServiceB.java`**: Contains methods that create spans and receive communication from `OpenTelemetryServiceA`.
- **`RedisHelper.java`**: Helper class for managing Redis Streams communication and trace context propagation.

### Dependencies

The project uses the following dependencies:

- **OpenTelemetry API** (`io.opentelemetry:opentelemetry-api`)
- **OpenTelemetry SDK** (`io.opentelemetry:opentelemetry-sdk`)
- **OTLP Exporter** (`io.opentelemetry:opentelemetry-exporter-otlp`)
- **gRPC** (`io.grpc:grpc-netty-shaded`)
- **Lettuce Redis Client** (`io.lettuce.core:lettuce-core`)

These dependencies are managed through Gradle.

### Build and Run

To build and run the examples:

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

   This will run the `MainApplication` which triggers both examples.

### License

This project is licensed under the MIT License.
