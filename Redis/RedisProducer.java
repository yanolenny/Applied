package com.redis.example;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind; // Correct import for SpanKind
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.extension.annotations.WithSpan;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.XAddParams;

import java.util.HashMap;
import java.util.Map;

public class RedisProducer {

    private static final String REDIS_STREAM = "mystream";
    private static final W3CTraceContextPropagator propagator = W3CTraceContextPropagator.getInstance();

    public static void main(String[] args) {
        log("Starting RedisProducer with context propagation...");

        try (Jedis jedis = new Jedis("localhost", 6379)) {
            produceMessage(jedis);
        }

        log("RedisProducer finished");
    }

    @WithSpan
    public static void produceMessage(Jedis jedis) {
        log("Preparing to produce message...");

        Span producerSpan = GlobalOpenTelemetry.getTracer("redis-producer")
            .spanBuilder("redis-producer-span")
            .setSpanKind(SpanKind.CLIENT) // Use SpanKind.CLIENT instead of Span.Kind.CLIENT
            .startSpan();

        // Add version attribute
        producerSpan.setAttribute("version", "v-10.05-propagation");

        try (Scope scope = producerSpan.makeCurrent()) {
            Map<String, String> message = new HashMap<>();
            message.put("data", "Message from RedisProducer");
            message.put("random-attribute", String.valueOf((int) (Math.random() * 100)));

            // Inject the current context into the message
            log("Injecting context into the Redis message...");
            propagator.inject(Context.current(), message, MapTextMapSetter.INSTANCE);

            log("Sending message with context...");
            jedis.xadd(REDIS_STREAM, XAddParams.xAddParams(), message);
        } finally {
            producerSpan.end();
            log("Producer span ended");
        }
    }

    private static void log(String message) {
        System.out.println(System.currentTimeMillis() + " - RedisProducer: " + message);
    }

    // Helper to set the context in the map
    static class MapTextMapSetter implements TextMapSetter<Map<String, String>> {
        static final MapTextMapSetter INSTANCE = new MapTextMapSetter();

        @Override
        public void set(Map<String, String> carrier, String key, String value) {
            carrier.put(key, value);
        }
    }
}
