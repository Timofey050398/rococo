package timofeyqa.rococo.config;

import javax.annotation.Nonnull;
import java.util.List;

public interface Config {

  static Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
      ? DockerConfig.INSTANCE
      : LocalConfig.INSTANCE;
  }

  String frontUrl();

  String authUrl();

  String authJdbcUrl();

  String jdbcUrl();

  String gatewayUrl();

  String userdataUrl();

  String museumGrpcUrl();

  String geoGrpcUrl();

  String artistGrpcUrl();

  String paintingGrpcUrl();

  default String ghUrl(){
    return "https://api.github.com/";
  }

  @Nonnull
  default String defaultPassword(){
    return "12345";
  }

  default int museumGrpcPort(){
    return 8092;
  }

  default int paintingGrpcPort(){
    return 8094;
  }

  default int geoGrpcPort(){
    return 8095;
  }

  default int artistGrpcPort(){
    return 8091;
  }

  default int dbPort(){
    return 3306;
  }

  default int gatewayPort(){
    return 8090;
  }
  default int userdataPort(){
    return 8089;
  }
  default int authPort(){
    return 9000;
  }

  @Nonnull
  String allureDockerServiceUrl();

  @Nonnull
  String screenshotBaseDir();

  @Nonnull
  String kafkaAddress();

  default List<String> kafkaTopics() {
    return List.of("users","logs");
  }
}
