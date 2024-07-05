package guru.bonacci.selector.gensource;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import guru.bonacci.selector.Vehicle;

final class VehicleIterator implements Iterator<Vehicle>, Serializable {

    private static final long serialVersionUID = 1L;

    public VehicleIterator() {
    }

    static VehicleIterator unbounded() {
        return new VehicleIterator();
    }
    @Override
    public boolean hasNext() {
      return true;
    }

    @Override
    public Vehicle next() {
        return new Vehicle(1, randomApple, System.currentTimeMillis(), randomPriority);
    }

    private static Supplier<Integer> randomPriority = () -> new Random().nextInt(10);

    private static List<String> apples = List.of("A", "B", "C", "D");
    private static Supplier<String> randomApple = () -> apples.get(new Random().nextInt(apples.size()));
}
