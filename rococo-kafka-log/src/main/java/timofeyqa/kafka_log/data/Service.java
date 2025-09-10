package timofeyqa.kafka_log.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Service {
  rococo_auth,
  rococo_gateway,
  rococo_museum,
  rococo_artist,
  rococo_painting,
  rococo_userdata,
  rococo_geo;

  @JsonCreator
  public static Service fromValue(String value) {
    return Service.valueOf(value.replace("-", "_"));
  }

  @JsonValue
  public String toValue() {
    return this.name().replace("_", "-");
  }
}

