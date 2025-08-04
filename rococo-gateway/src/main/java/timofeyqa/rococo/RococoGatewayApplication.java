package timofeyqa.rococo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import timofeyqa.rococo.service.PropertiesLogger;

@SpringBootApplication
@EnableCaching
public class RococoGatewayApplication {

  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(RococoGatewayApplication.class);
    springApplication.addListeners(new PropertiesLogger());
    springApplication.run(args);
  }

}
