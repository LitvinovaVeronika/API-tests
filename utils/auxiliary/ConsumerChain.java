package tests.utils.auxiliary;

import tests.utils.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ConsumerChain {

    private final List<ChainElement> consumers = new LinkedList<>();

    private <T> ConsumerChain(T targetObject, Utils.CheckedConsumer<T> consumer, Predicate<T> predicate) {
        consumers.add(new ChainElement(targetObject, consumer, predicate));
    }

    public static <T> ConsumerChain from(T targetObject, Utils.CheckedConsumer<T> consumer) {
        return from(targetObject, consumer, null);
    }

    public static <T> ConsumerChain from(T targetObject, Utils.CheckedConsumer<T> consumer, Predicate<T> predicate) {
        return new ConsumerChain(targetObject, consumer, predicate);
    }

    public <T> ConsumerChain then(T targetObject, Utils.CheckedConsumer<T> consumer) {
        return then(targetObject, consumer, null);
    }

    public <T> ConsumerChain then(T targetObject, Utils.CheckedConsumer<T> consumer, Predicate<T> predicate) {
        consumers.add(new ChainElement(targetObject, consumer, predicate));
        return this;
    }

    public void invoke() throws Exception {
        Exception exception = null;
        for (ChainElement consumer : consumers) {
            try {
                consumer.tryInvoke();
            } catch (Throwable t) {
                if (isNull(exception)) {
                    exception = new Exception();
                }
                exception.addSuppressed(t);
            }
        }
        if (nonNull(exception)) {
            throw exception;
        }
    }

    private static class ChainElement<T> {

        private final T targetObject;
        private final Utils.CheckedConsumer<T> consumer;
        private final Predicate<T> predicate;

        private ChainElement(T targetObject, Utils.CheckedConsumer<T> consumer, Predicate<T> predicate) {
            this.targetObject = targetObject;
            this.consumer = consumer;
            this.predicate = predicate;
        }

        public void tryInvoke() throws Exception {
            if (isNull(predicate) || predicate.test(targetObject)) {
                consumer.accept(targetObject);
            }
        }

    }

}
