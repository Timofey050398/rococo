package timofeyqa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import timofeyqa.grpc.rococo.Artist;
import timofeyqa.grpc.rococo.PageArtistResponse;
import timofeyqa.rococo.config.RococoGatewayServiceConfig;
import timofeyqa.rococo.model.page.RestPage;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder=true)
public record ArtistJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("name")
    String name,
    @JsonProperty("biography")
    @Size(max = 2000, message = "Biography name can`t be longer than 2000 characters")
    String biography,
    @JsonProperty("photo")
    @Size(max = RococoGatewayServiceConfig.ONE_MB)
    String photo) {

  public static @Nonnull ArtistJson fromGrpc(Artist grpc) {
    final String photo = grpc.getPhoto().isEmpty()
            ? null
            : new String(grpc.getPhoto().toByteArray(), StandardCharsets.UTF_8);
    return new ArtistJson(
        UUID.fromString(grpc.getId()),
        grpc.getName(),
        grpc.getBiography(),
        photo
    );
  }

    public Artist toGrpc() {
        Artist.Builder builder = Artist.newBuilder().setId(id.toString());

        if (!StringUtils.isEmpty(name)) {
            builder.setName(name);
        }

        if (!StringUtils.isEmpty(biography)) {
            builder.setBiography(biography);
        }

        if (photo != null && !photo.isEmpty()) {
            builder.setPhoto(ByteString.copyFrom(photo.getBytes(StandardCharsets.UTF_8)));
        }

        return builder.build();
    }


  public static @Nonnull RestPage<ArtistJson> fromGrpcPage(@Nonnull PageArtistResponse grpc, @Nonnull Pageable pageable) {
    List<ArtistJson> content = grpc.getArtistsList()
            .stream()
            .map(ArtistJson::fromGrpc)
            .toList();

    return new RestPage<>(
            content,
            pageable,
            grpc.getTotalElements()
    );
  }

  public ArtistJson(UUID id){
      this(id,null,null,null);
  }
}
