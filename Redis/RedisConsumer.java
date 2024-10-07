package com.redis.example;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStreamCommands;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.XReadArgs;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind; // Correct import for SpanKind
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.extension.annotations.WithSpan;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.TextMapGetter;

import java.util.List;
import java.util.Map;

public class RedisConsumer {

    private static final String REDIS_STREAM = "mystream";
    private static final W3CTraceContextPropagator propagator = W3CTraceContextPropagator.getInstance();

    public static void main(String[] args) {
        log("Starting RedisConsumer with context propagation...");

        RedisClient redisClient = RedisClient.create(RedisURI.create("redis://localhost:6379"));
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStreamCommands<String, String> streamCommands = connection.sync();
            consumeMessage(streamCommands);
        }

        redisClient.shutdown();
        log("RedisConsumer finished");
    }

    @WithSpan
    public static void consumeMessage(RedisStreamCommands<String, String> streamCommands) {
        List<StreamMessage<String, String>> messages = streamCommands.xread(
                XReadArgs.Builder.count(1), XReadArgs.StreamOffset.from(REDIS_STREAM, "0"));

        if (!messages.isEmpty()) {
            StreamMessage<String, String> message = messages.get(0);
            log("Processing message: " + message.getBody());

            // Extract context from the Redis message
            log("Extracting context from the Redis message...");
            Context extractedContext = propagator.extract(Context.current(), message.getBody(), MapTextMapGetter.INSTANCE);

            Span consumerSpan = GlobalOpenTelemetry.getTracer("redis-consumer")
                .spanBuilder("redis-consumer-span")
                .setParent(extractedContext)
                .setSpanKind(SpanKind.SERVER) // Use SpanKind.SERVER
                .startSpan();

            // Add no attributes in the Consumer span to validate context propagation
            // Log the context to verify that it contains the propagated attributes
            log("Consumer span context: Trace ID = " + consumerSpan.getSpanContext().getTraceId());
            log("Consumer span context: Span ID = " + consumerSpan.getSpanContext().getSpanId());

            try (Scope scope = consumerSpan.makeCurrent()) {
                log("Random attribute: " + message.getBody().get("random-attribute"));
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                logError("Error during sleep: " + e.getMessage());
                Thread.currentThread().interrupt();
            } finally {
                consumerSpan.end();
                log("Consumer span ended");
            }

            streamCommands.xdel(REDIS_STREAM, message.getId());
            log("Deleted message from Redis stream with ID: " + message.getId());
        } else {
            log("No messages to consume.");
        }
    }

    private static void log(String message) {
        System.out.println(System.currentTimeMillis() + " - RedisConsumer: " + message);
    }

    private static void logError(String message) {
        System.err.println(System.currentTimeMillis() + " - RedisConsumer: ERROR - " + message);
    }

    // Helper to extract the context from the map
    static class MapTextMapGetter implements TextMapGetter<Map<String, String>> {
        static final MapTextMapGetter INSTANCE = new MapTextMapGetter();

        @Override
        public Iterable<String> keys(Map<String, String> carrier) {
            return carrier.keySet();
        }

        @Override
        public String get(Map<String, String> carrier, String key) {
            return carrier.get(key);
        }
    }
}
