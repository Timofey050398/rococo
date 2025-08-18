package timofeyqa.rococo.config;

enum DockerConfig implements Config {
  INSTANCE;

  @Override
  public String frontUrl() {
    return "http://frontend.rococo.dc/";
  }

  @Override
  public String authUrl() {
    return toUrlStr("auth.rococo.dc",authPort());
  }

  @Override
  public String authJdbcUrl() {
    return toMySqlStr()+"rococo-auth";
  }

  @Override
  public String jdbcUrl() {
    return toMySqlStr()+"rococo";
  }

  @Override
  public String gatewayUrl() {
    return toUrlStr("gateway.rococo.dc",gatewayPort());
  }

  @Override
  public String userdataUrl() {
    return toUrlStr("userdata.rococo.dc",userdataPort());
  }

  @Override
  public String museumGrpcUrl() {
    return "museum.rococo.dc";
  }

  @Override
  public String geoGrpcUrl() {
    return "geo.rococo.dc";
  }

  @Override
  public String artistGrpcUrl() {
    return "artist.rococo.dc";
  }

  @Override
  public String paintingGrpcUrl() {
    return "painting.rococo.dc";
  }

  private String toUrlStr(String host, int port){
    return compileStr("http",host,port);
  }

  private String toMySqlStr(){
    return compileStr("jdbc:mysql","127.0.0.1",3306);
  }

  private String compileStr(String type, String host, int port){
    return String.format("%s://%s:%d/", type, host, port);
  }
}
