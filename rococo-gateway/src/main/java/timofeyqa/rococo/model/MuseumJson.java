package timofeyqa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import timofeyqa.grpc.rococo.Museum;
import timofeyqa.grpc.rococo.PageMuseum;
import timofeyqa.rococo.config.RococoGatewayServiceConfig;
import timofeyqa.rococo.model.page.RestPage;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
public record MuseumJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("title")
    @Size(max = 256, message = "Title name can`t be longer than 256 characters")
    String title,
    @JsonProperty("description")
    @Size(max = 1000, message = "Description name can`t be longer than 1000 characters")
    String description,
    @JsonProperty("photo")
    @Size(max = RococoGatewayServiceConfig.ONE_MB)
    String photo,
    @JsonProperty("geo")
    GeoJson geo) {

  public static @Nonnull MuseumJson fromGrpc(Museum grpc) {
    final String photo = grpc.getPhoto().isEmpty()
            ? null
            : new String(grpc.getPhoto().toByteArray(), StandardCharsets.UTF_8);
    return new MuseumJson(
        UUID.fromString(grpc.getId()),
        grpc.getTitle(),
        grpc.getDescription(),
        photo,
        new GeoJson(
                grpc.getCity(),
                UUID.fromString(grpc.getCountryId())
        )
    );
  }

    public Museum toGrpc() {
        Museum.Builder builder = Museum.newBuilder().setId(id.toString());

        if (!StringUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        if (!StringUtils.isEmpty(description)) {
            builder.setDescription(description);
        }

        if (photo != null && !photo.isEmpty()) {
            builder.setPhoto(ByteString.copyFrom(photo.getBytes(StandardCharsets.UTF_8)));
        }

        if (geo != null) {
            if (!StringUtils.isEmpty(geo.city())) {
                builder.setCity(geo.city());
            }
            if (geo.country() != null && geo.country().id() != null) {
                builder.setCountryId(geo.country().id().toString());
            }
        }

        return builder.build();
    }


  public static @Nonnull RestPage<MuseumJson> fromGrpcPage(@Nonnull PageMuseum grpc, @Nonnull Pageable pageable) {
    List<MuseumJson> content = grpc.getMuseumsList()
            .stream()
            .map(MuseumJson::fromGrpc)
            .toList();

    return new RestPage<>(
            content,
            pageable,
            grpc.getTotalElements()
    );
  }

  public MuseumJson(UUID id){
      this(id, null,null,null,null);
  }
}
