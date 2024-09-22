
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

## Detailed Explanation

In this example, we use `ExecutorService` to run tasks in multiple threads. Each task creates a child span with the parent span passed from the main thread. OpenTelemetry ensures that the trace context is propagated correctly across threads.

### 1. Setting Up the Tracer

We use the `GlobalOpenTelemetry.getTracer()` to obtain a tracer instance that allows us to create spans.

```java
private static final Tracer tracer = GlobalOpenTelemetry.getTracer("multithreading-example");
```

### 2. Creating the Parent Span in the Main Thread

In the `main()` method, we start a parent span that represents the main task in the main thread. This parent span will have child spans created by tasks running in separate threads.

```java
Span parentSpan = tracer.spanBuilder("MainThreadSpan").startSpan();
try (Scope scope = parentSpan.makeCurrent()) {
    System.out.println("Starting the parent span in the main thread");
    
    // Submit tasks to the thread pool
    for (int i = 0; i < 3; i++) {
        executor.submit(new WorkerTask("Task-" + (i + 1), Context.current()));
    }
} finally {
    parentSpan.end();
}
```

Here, we:
- Start the span using `spanBuilder()`.
- Use a `Scope` to ensure the parent span is set as the current context.
- Pass the current context (`Context.current()`) to each task so they can propagate the parent span's trace.

### 3. Propagating the Trace Context Across Threads

Each task is represented by the `WorkerTask` class, which implements `Runnable`. The `WorkerTask` constructor takes the current trace context and a task name. The child span is created with the parent context.

```java
@Override
public void run() {
    Span span = tracer.spanBuilder(taskName)
            .setParent(parentContext)
            .startSpan();

    try (Scope scope = span.makeCurrent()) {
        System.out.println(taskName + " is executing in thread: " + Thread.currentThread().getName());
        
        // Simulate some work
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    } finally {
        span.end();
    }
}
```

- **`setParent(parentContext)`**: This ensures that the new span is a child of the parent span from the main thread.
- **`try (Scope scope = span.makeCurrent())`**: Ensures that the child span is the current context for the work done inside the thread.
- **`span.end()`**: The span is ended when the task finishes, ensuring it is properly closed.

### 4. Simulating Multithreaded Execution

We use an `ExecutorService` to manage a pool of worker threads. Each task is submitted to the thread pool, and the trace context is propagated from the main thread to the worker threads.

```java
ExecutorService executor = Executors.newFixedThreadPool(3);
for (int i = 0; i < 3; i++) {
    executor.submit(new WorkerTask("Task-" + (i + 1), Context.current()));
}
```

### 5. Shutting Down the Executor

After submitting all tasks, we gracefully shut down the executor and wait for all tasks to finish.

```java
executor.shutdown();
executor.awaitTermination(10, TimeUnit.SECONDS);
```

This ensures that the program waits for the threads to complete before exiting.

## Running the Example

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

## Project Structure

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
