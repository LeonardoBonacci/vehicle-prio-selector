package spendreport;


import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Vehicle {

    private long numberplate;
    private String apple;
    private long timestamp;
    private int priority;
    
    Vehicle(long numberplate, Supplier<String> appleSupplier, long timestamp, Supplier<Integer> prioritySupplier) {
    	this(numberplate, appleSupplier.get(), timestamp, prioritySupplier.get());
    }
}
