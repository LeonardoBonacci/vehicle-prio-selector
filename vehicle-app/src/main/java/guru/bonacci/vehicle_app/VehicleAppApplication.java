package guru.bonacci.vehicle_app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class VehicleAppApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(VehicleAppApplication.class, args);
	}

	private static PrimitiveIterator.OfInt numberPlates = IntStream.iterate(1, num -> num + 1).iterator();
	
  private static Supplier<Integer> randomPriority = () -> new Random().nextInt(10);

  private static List<String> apples = List.of("A"); // easy testing..
  private static Supplier<String> randomApple = () -> apples.get(new Random().nextInt(apples.size()));
  
	@Override
	public void run(String... args) throws Exception {
		
		while (true) {
		 try {
			 long now = System.currentTimeMillis();
       String content = String.format("%d,%s,%d,%d", 
      		 																numberPlates.next(), 
      		 																randomApple.get(), 
      		 																System.currentTimeMillis(), 
      		 																randomPriority.get());
	
       System.out.println(content);
       File file = new File(String.format("/tmp/flink/%d.csv", now));
	
       if (!file.exists()) {
           file.createNewFile();
       }
	
       FileWriter fw = new FileWriter(file.getAbsoluteFile());
       BufferedWriter bw = new BufferedWriter(fw);
       bw.write(content);
       bw.close();
       
       Thread.sleep(1000);
	
	   } catch (IOException e) {
	       log.error(e.getMessage());
	   }
		} 
	}

	@Service
	public class FileDeletion {

    @Scheduled(cron = "0/10 * * ? * *")
    public void delete() throws IOException {
  		long oneMinAgo = System.currentTimeMillis() - 60000;;
    	
      File folder = new File("/tmp/flink");
      Function<File, Boolean> regexMatcher = (file) -> Pattern.compile(".*.csv")
                .matcher(file.getName()).find();
      File[] files = folder.listFiles();
 
      System.out.println("Deleting Files at ts " + System.currentTimeMillis());
      Arrays.stream(files).filter(regexMatcher::apply)
      			.map(file -> file.getName().replace(".csv", ""))
            .peek(System.out::println)
      			.map(Long::valueOf)
      			.filter(timestamp -> timestamp < oneMinAgo)
      			.map(fileName -> new File(folder + "/" + fileName + ".csv"))
            .peek(System.out::println)
            .forEach(File::delete);
      System.out.println("Files deleted");
    } 	
	}
}
