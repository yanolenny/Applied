import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.RedisClient;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import io.opentelemetry.context.Context;
import io.opentelemetry.api.trace.Span;

import java.util.HashMap;
import java.util.Map;

public class RedisHelper {

    private final RedisCommands<String, String> redisCommands;

    public RedisHelper() {
        RedisClient redisClient = RedisClient.create("redis://localhost:6379");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        redisCommands = connection.sync();
    }

    public void sendMessageWithContext(String stream, String messageKey, Map<String, String> traceContext) {
        Map<String, String> message = new HashMap<>(traceContext);
        message.put("messageKey", messageKey);
        redisCommands.xadd(stream, message);
    }

    public Map<String, String> receiveMessage(String stream) {
        return redisCommands.xrange(stream, "-", "+").get(0).getBody();
    }

    public static class TextMapPropagator implements TextMapGetter<Map<String, String>>, TextMapSetter<Map<String, String>> {

        @Override
        public Iterable<String> keys(Map<String, String> carrier) {
            return carrier.keySet();
        }

        @Override
        public String get(Map<String, String> carrier, String key) {
            return carrier.get(key);
        }

        @Override
        public void set(Map<String, String> carrier, String key, String value) {
            carrier.put(key, value);
        }
    }
}
