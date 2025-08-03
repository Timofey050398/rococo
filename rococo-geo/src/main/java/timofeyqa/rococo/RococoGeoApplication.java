package timofeyqa.rococo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import timofeyqa.rococo.service.PropertiesLogger;

@SpringBootApplication
public class RococoGeoApplication {
	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(RococoGeoApplication.class);
		springApplication.addListeners(new PropertiesLogger());
		springApplication.run(args);
	}
}
