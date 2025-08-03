package timofeyqa.rococo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import timofeyqa.rococo.service.PropertiesLogger;

@SpringBootApplication
public class RococoGatewayApplication {

  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(RococoGatewayApplication.class);
    springApplication.addListeners(new PropertiesLogger());
    springApplication.run(args);
  }

}
