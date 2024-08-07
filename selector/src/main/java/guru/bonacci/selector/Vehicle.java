package guru.bonacci.selector;


import java.io.Serializable;
import java.util.function.Supplier;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"numberplate", "apple", "timestamp", "priority"})
public final class Vehicle implements Serializable {

    private static final long serialVersionUID = 1L;
    
		private long numberplate;
    private String apple;
    private long timestamp;
    private int priority;
    
    public Vehicle(long numberplate, Supplier<String> appleSupplier, long timestamp, Supplier<Integer> prioritySupplier) {
    	this(numberplate, appleSupplier.get(), timestamp, prioritySupplier.get());
    }
}
