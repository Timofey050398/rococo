package timofeyqa.rococo.mappers;

import org.springframework.stereotype.Component;
import timofeyqa.grpc.rococo.GeoResponse;
import timofeyqa.rococo.model.CountryJson;

import java.util.UUID;

@Component
public class CountryMapper {
  public static CountryJson fromGrpc(GeoResponse response) {
          return new CountryJson(
              response.getId().isBlank() ? null : UUID.fromString(response.getId()),
              response.getName().isBlank() ? null : response.getName()
          );
  }
}
