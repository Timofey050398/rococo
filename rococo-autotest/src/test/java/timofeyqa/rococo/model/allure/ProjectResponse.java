package timofeyqa.rococo.model.allure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProjectResponse(
    @JsonProperty
    Data data,
    @JsonProperty("meta_data")
    MetaData metaData
) {
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Data(
      @JsonProperty Map<String,Project> projects
  ) {}
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Project(
      @JsonProperty String uri
  ) {
  }
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record MetaData(
      @JsonProperty String message
  ) {}
}
