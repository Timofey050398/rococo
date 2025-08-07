package timofeyqa.rococo.mappers;

import com.google.protobuf.ByteString;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import timofeyqa.grpc.rococo.AddArtistRequest;
import timofeyqa.grpc.rococo.AddPaintingRequest;
import timofeyqa.grpc.rococo.PagePainting;
import timofeyqa.grpc.rococo.Painting;
import timofeyqa.rococo.model.ArtistJson;
import timofeyqa.rococo.model.MuseumJson;
import timofeyqa.rococo.model.PaintingJson;
import timofeyqa.rococo.model.page.RestPage;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component
public class PaintingMapper {
  public static @Nonnull PaintingJson fromGrpc(@Nonnull Painting grpc) {
    final String content = grpc.getContent().isEmpty()
            ? null
            : new String(grpc.getContent().toByteArray(), StandardCharsets.UTF_8);
    return new PaintingJson(
        grpc.getId().isBlank() ? null : UUID.fromString(grpc.getId()),
        grpc.getTitle().isBlank() ? null : grpc.getTitle(),
        grpc.getDescription().isBlank() ? null : grpc.getDescription(),
        grpc.getArtistId().isBlank() ? null : new ArtistJson(UUID.fromString(grpc.getArtistId())),
        grpc.getMuseumId().isBlank() ? null : new MuseumJson(UUID.fromString(grpc.getMuseumId())),
        content
    );
  }

  public static Painting toGrpc(PaintingJson painting) {
    Painting.Builder builder = Painting.newBuilder();

    if(painting.id() != null){
      builder.setId(painting.id().toString());
    }

    if (!StringUtils.isEmpty(painting.title())) {
      builder.setTitle(painting.title());
    }

    if (!StringUtils.isEmpty(painting.description())) {
      builder.setDescription(painting.description());
    }

    if (painting.content() != null && !painting.content().isEmpty()) {
      builder.setContent(ByteString.copyFrom(painting.content().getBytes(StandardCharsets.UTF_8)));
    }

    if (painting.museum() != null && painting.museum().id() != null) {
      builder.setMuseumId(painting.museum().id().toString());
    }

    if (painting.artist() != null && painting.artist().id() != null) {
      builder.setArtistId(painting.artist().id().toString());
    }

    return builder.build();
  }
  public static AddPaintingRequest toPostGrpc(PaintingJson json) {
    Painting painting = toGrpc(json);
    return AddPaintingRequest.newBuilder()
        .setTitle(painting.getTitle())
        .setDescription(painting.getDescription())
        .setContent(painting.getContent())
        .setMuseumId(painting.getMuseumId())
        .setArtistId(painting.getArtistId())
        .build();
  }

  public static @Nonnull RestPage<PaintingJson> fromGrpcPage(@Nonnull PagePainting grpc, @Nonnull Pageable pageable) {
      List<PaintingJson> content = grpc.getPaintingsList()
              .stream()
              .map(PaintingMapper::fromGrpc)
              .toList();

      return new RestPage<>(
              content,
              pageable,
              grpc.getTotalElements()
      );
  }
}
