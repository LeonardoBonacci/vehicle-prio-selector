package guru.bonacci.selector;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.csv.CsvReaderFormat;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.getindata.connectors.http.HttpSink;
import com.getindata.connectors.http.internal.sink.HttpSinkRequestEntry;

public class VehicleSelectionJob {
	
	@SuppressWarnings({ "serial" })
	public static void main(String[] args) throws Exception, JsonProcessingException {
		
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		final FileSource<Vehicle> source =
        FileSource.forRecordStreamFormat(CsvReaderFormat.forPojo(Vehicle.class), Path.fromLocalFile(new File("/tmp/flink")))
        .monitorContinuously(Duration.ofSeconds(5))  
        .build();

		WatermarkStrategy<Vehicle> watermarkStrategy = WatermarkStrategy
	    .<Vehicle>forBoundedOutOfOrderness(Duration.ofSeconds(10))
	    .withIdleness(Duration.ofMinutes(1))
  	  .withTimestampAssigner((vehicle, timestamp) -> vehicle.getTimestamp()); 
	
		DataStream<Vehicle> vehicles = 
			env
				.fromSource(source, watermarkStrategy, "vehicle-file-input")
				.name("vehicle-source");
		
//		vehicles
//				.print();

//		DataStream<Vehicle> vehicles = 
//			env
//			.addSource(new VehicleSource())
//			.assignTimestampsAndWatermarks(WatermarkStrategy
//	        .<Vehicle>forMonotonousTimestamps()
//	        .withTimestampAssigner((vehicle, timestamp) -> vehicle.getTimestamp()))
//			.name("vehicles");
//
		DataStream<Vehicle> selectedVehicles = 
			vehicles
				.keyBy(Vehicle::getApple)
				.window(TumblingEventTimeWindows.of(Duration.ofSeconds(5)))
				.trigger(CountTrigger.of(3))
				.reduce(new ReduceFunction<Vehicle>() {
					  public Vehicle reduce(Vehicle v1, Vehicle v2) {
			        return v1.getPriority() >= v2.getPriority() ? v1 : v2;
			      }
		    })
				.name("vehicle-selector");
		
		selectedVehicles.print();

		HttpSink<String> poster = HttpSink.<String>builder()
	    .setEndpointUrl("http://vehicle-app:8080/foo")
	    .setProperty("gid.connector.http.sink.header.Content-Type", "application/json")
	    .setElementConverter(
	        (v, _context) -> new HttpSinkRequestEntry("POST", v.getBytes(StandardCharsets.UTF_8)))
	    .build();

		vehicles
			.map(vehicle -> new ObjectMapper().writeValueAsString(vehicle))
			.sinkTo(poster);

		env.execute("Vehicle Selection");
	}
}
