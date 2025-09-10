package timofeyqa.kafka_log.data;

public enum Service {
  rococo_auth,rococo_gateway,rococo_museum,rococo_artist,rococo_painting, rococo_userdata, rococo_geo;

  @Override
  public String toString() {
    return this.name().replaceAll("_", "-");
  }
}
