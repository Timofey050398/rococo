package timofeyqa.rococo.config;

import org.jetbrains.annotations.NotNull;

enum LocalConfig implements Config {
  INSTANCE;

  @Override
  public String frontUrl() {
    return toUrlStr(3000);
  }

  @Override
  public String authUrl() {
    return toUrlStr(authPort());
  }

  @Override
  public String authJdbcUrl() {
    return toMySqlStr(dbPort())+"rococo-auth";
  }

  @Override
  public String jdbcUrl() {
    return toMySqlStr(dbPort())+"rococo";
  }

  @Override
  public String gatewayUrl() {
    return toUrlStr(gatewayPort());
  }

  @Override
  public String userdataUrl() {
    return toUrlStr(userdataPort());
  }

  @Override
  public String museumGrpcUrl() {
    return localhost();
  }

  @Override
  public String geoGrpcUrl() {
    return localhost();
  }

  @Override
  public String artistGrpcUrl() {
    return localhost();
  }

  @Override
  public String paintingGrpcUrl() {
    return localhost();
  }

  private String localhost(){
    return "127.0.0.1";
  }

  private String toUrlStr(int port){
    return compileStr("http",port);
  }

  private String toMySqlStr(int port){
    return compileStr("jdbc:mysql",port);
  }

  private String compileStr(String type, int port){
    return String.format("%s://%s:%d/", type, localhost(),port);
  }

  @NotNull
  @Override
  public String allureDockerServiceUrl() {
    return "http://127.0.0.1:5050/";
  }

  @NotNull
  @Override
  public String screenshotBaseDir() {
    return "screenshots/local/";
  }
}
