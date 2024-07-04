package spendreport;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import lombok.NoArgsConstructor;

@NoArgsConstructor
final class VehicleIterator implements Iterator<Vehicle>, Serializable {

    private static final long serialVersionUID = 1L;


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
