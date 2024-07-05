package guru.bonacci.vehicle_app;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Vehicle implements Serializable {

	private static final long serialVersionUID = 1L;

	private long numberplate;
	private String apple;
	private long timestamp;
	private int priority;
}
