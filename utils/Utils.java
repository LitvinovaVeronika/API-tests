package tests.utils;

import io.restassured.response.ExtractableResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import static io.restassured.path.json.JsonPath.from;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

public class Utils {

    public static Map<String, Object> getObjectMap(ExtractableResponse response) {
        return (Map) from(response.asString()).get();
    }

    public static <T> void invokeNotNull(T targetObject, CheckedConsumer<T> consumer) throws Exception {
        if (nonNull(targetObject)) {
            consumer.accept(targetObject);
        }
    }

    public static Map<String, Object> getObjectFromListById(List<Map<String, Object>> list, String id) {
        return list.stream()
                .filter(object -> id.equals(object.get("id")))
                .findAny()
                .orElse(null);
    }

    public static List<Map<String, Object>> getListFromResponseByKey(ExtractableResponse response, String key) {
        return (List<Map<String, Object>>) getObjectMap(response).get(key);
    }

    @FunctionalInterface
    public interface CheckedConsumer<T> {
        void accept(T t) throws Exception;
    }

    public static <T> void waitUntil(T target, Predicate<T> predicate, long timeout)
            throws InterruptedException, TimeoutException {
        if (timeout <= 0L) {
            throw new IllegalArgumentException("Timeout must be > 0");
        }
        long startTime = currentTimeMillis();
        while (!predicate.test(target)) {

            checkTimeout(startTime, timeout);

            TimeUnit.SECONDS.sleep(1);
        }
    }

    private static void checkTimeout(long startTime, long timeout) throws TimeoutException {
        if (timeout > 0L && currentTimeMillis() > startTime + timeout) {
            throw new TimeoutException(String.format("timed out after %s milliseconds", timeout));
        }
    }
}
