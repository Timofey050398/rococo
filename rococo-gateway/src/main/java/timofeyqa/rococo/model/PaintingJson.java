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
import timofeyqa.grpc.rococo.PagePainting;
import timofeyqa.grpc.rococo.Painting;
import timofeyqa.rococo.config.RococoGatewayServiceConfig;
import timofeyqa.rococo.model.page.RestPage;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
public record PaintingJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("title")
    @Size(max = 256, message = "Title name can`t be longer than 256 characters")
    String title,
    @JsonProperty("description")
    @Size(max = 1000, message = "Description name can`t be longer than 1000 characters")
    String description,
    @JsonProperty("artist")
    ArtistJson artist,
    @JsonProperty("museum")
    MuseumJson museum,
    @Size(max = RococoGatewayServiceConfig.ONE_MB)
    @JsonProperty("content")
    String content) {

  public static @Nonnull PaintingJson fromGrpc(@Nonnull Painting grpc) {
    final String content = grpc.getContent().isEmpty()
            ? null
            : new String(grpc.getContent().toByteArray(), StandardCharsets.UTF_8);
    return new PaintingJson(
        UUID.fromString(grpc.getId()),
        grpc.getTitle(),
        grpc.getDescription(),
        new ArtistJson(UUID.fromString(grpc.getArtistId())),
        new MuseumJson(UUID.fromString(grpc.getMuseumId())),
        content
    );
  }

    public Painting toGrpc() {
        Painting.Builder builder = Painting.newBuilder().setId(id.toString());

        if (!StringUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        if (!StringUtils.isEmpty(description)) {
            builder.setDescription(description);
        }

        if (content != null && !content.isEmpty()) {
            builder.setContent(ByteString.copyFrom(content.getBytes(StandardCharsets.UTF_8)));
        }

        if (museum != null && museum.id() != null) {
            builder.setMuseumId(museum.id().toString());
        }

        if (artist != null && artist.id() != null) {
            builder.setMuseumId(artist.id().toString());
        }

        return builder.build();
    }

  public static @Nonnull RestPage<PaintingJson> fromGrpcPage(@Nonnull PagePainting grpc, @Nonnull Pageable pageable) {
      List<PaintingJson> content = grpc.getPaintingsList()
              .stream()
              .map(PaintingJson::fromGrpc)
              .toList();

      return new RestPage<>(
              content,
              pageable,
              grpc.getTotalElements()
      );
  }

}
