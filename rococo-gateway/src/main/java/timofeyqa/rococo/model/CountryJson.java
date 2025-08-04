package timofeyqa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import timofeyqa.grpc.rococo.GeoResponse;

import java.util.UUID;

@Builder(toBuilder = true)
public record CountryJson(
        @JsonProperty("id") UUID id,
        @JsonProperty("name") String name
        ) {

        public static CountryJson blank(){
           return new CountryJson(null, null);
        }

    public static CountryJson fromGrpc(GeoResponse response) {
            return new CountryJson(
                    UUID.fromString(response.getId()),
                    response.getName()
            );
    }
}
