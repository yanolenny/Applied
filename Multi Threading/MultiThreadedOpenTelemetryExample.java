
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiThreadedOpenTelemetryExample {

    private static final Tracer tracer = GlobalOpenTelemetry.getTracer("multithreading-example");

    public static void main(String[] args) throws InterruptedException {
        // Create a thread pool with 3 threads
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Start a parent span
        Span parentSpan = tracer.spanBuilder("MainThreadSpan").startSpan();
        try (Scope scope = parentSpan.makeCurrent()) {
            System.out.println("Starting the parent span in the main thread");

            // Pass trace context into a thread pool task
            for (int i = 0; i < 3; i++) {
                executor.submit(new WorkerTask("Task-" + (i + 1), Context.current()));
            }
        } finally {
            // End the parent span
            parentSpan.end();
        }

        // Shutdown the executor and wait for tasks to finish
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    // A task that runs in a separate thread, with trace context propagated
    static class WorkerTask implements Runnable {
        private final String taskName;
        private final Context parentContext;

        WorkerTask(String taskName, Context parentContext) {
            this.taskName = taskName;
            this.parentContext = parentContext;
        }

        @Override
        public void run() {
            // Start a new child span in the current thread, with the parent context
            Span span = tracer.spanBuilder(taskName)
                    .setParent(parentContext)
                    .startSpan();

            try (Scope scope = span.makeCurrent()) {
                System.out.println(taskName + " is executing in thread: " + Thread.currentThread().getName());

                // Simulate some work in the task
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // End the span after the task is done
                span.end();
            }
        }
    }
}
