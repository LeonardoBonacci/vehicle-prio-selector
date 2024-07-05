package guru.bonacci.selector.gensource;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.flink.streaming.api.functions.source.FromIteratorFunction;

import guru.bonacci.selector.Vehicle;

public class VehicleSource extends FromIteratorFunction<Vehicle> {

    private static final long serialVersionUID = 1L;

    public VehicleSource() {
        super(new RateLimitedIterator<>(VehicleIterator.unbounded()));
    }

    private static class RateLimitedIterator<T> implements Iterator<T>, Serializable {

        private static final long serialVersionUID = 1L;

        private final Iterator<T> inner;

        private RateLimitedIterator(Iterator<T> inner) {
            this.inner = inner;
        }

        @Override
        public boolean hasNext() {
            return inner.hasNext();
        }

        @Override
        public T next() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return inner.next();
        }
    }
}