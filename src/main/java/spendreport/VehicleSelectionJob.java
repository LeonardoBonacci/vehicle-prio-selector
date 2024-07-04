package spendreport;

import java.time.Duration;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;

public class VehicleSelectionJob {
	
	@SuppressWarnings({ "deprecation", "serial" })
	public static void main(String[] args) throws Exception {
		
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<Vehicle> vehicles = 
			env
			.addSource(new VehicleSource())
			.assignTimestampsAndWatermarks(WatermarkStrategy
	        .<Vehicle>forMonotonousTimestamps()
	        .withTimestampAssigner((vehicle, timestamp) -> vehicle.getTimestamp()))
			.name("vehicles");

		vehicles
			.keyBy(Vehicle::getApple)
			.window(SlidingEventTimeWindows.of(Duration.ofSeconds(5), Duration.ofSeconds(3)))
			.reduce(new ReduceFunction<Vehicle>() {
				  public Vehicle reduce(Vehicle v1, Vehicle v2) {
		        return v1.getPriority() >= v2.getPriority() ? v1 : v2;
		      }
	    })
			.name("vehicle-selector")
			.print();

		env.execute("Vehicle Selection");
	}
}
