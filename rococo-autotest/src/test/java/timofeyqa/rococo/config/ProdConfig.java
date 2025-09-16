package timofeyqa.rococo.config;

import org.jetbrains.annotations.NotNull;

public enum ProdConfig implements Config {
  INSTANCE;

  @Override
  public String frontUrl() {
    return "";
  }

  @Override
  public String authUrl() {
    return "";
  }

  @Override
  public String authJdbcUrl() {
    return "";
  }

  @Override
  public String jdbcUrl() {
    return "";
  }

  @Override
  public String gatewayUrl() {
    return "";
  }

  @Override
  public String userdataUrl() {
    return "";
  }

  @Override
  public String museumGrpcUrl() {
    return "";
  }

  @Override
  public String geoGrpcUrl() {
    return "";
  }

  @Override
  public String artistGrpcUrl() {
    return "";
  }

  @Override
  public String paintingGrpcUrl() {
    return "";
  }

  @NotNull
  @Override
  public String allureDockerServiceUrl() {
    return "";
  }

  @NotNull
  @Override
  public String screenshotBaseDir() {
    return "";
  }

  @NotNull
  @Override
  public String kafkaAddress() {
    return "";
  }
}
