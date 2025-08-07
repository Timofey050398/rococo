package timofeyqa.rococo.mappers;

import com.google.protobuf.ByteString;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import timofeyqa.grpc.rococo.AddMuseumRequest;
import timofeyqa.grpc.rococo.Museum;
import timofeyqa.grpc.rococo.PageMuseum;
import timofeyqa.rococo.model.GeoJson;
import timofeyqa.rococo.model.MuseumJson;
import timofeyqa.rococo.model.page.RestPage;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component
public class MuseumMapper {
  public static @Nonnull MuseumJson fromGrpc(Museum grpc) {
    final String photo = grpc.getPhoto().isEmpty()
            ? null
            : new String(grpc.getPhoto().toByteArray(), StandardCharsets.UTF_8);
    return new MuseumJson(
        grpc.getId().isBlank() ? null : UUID.fromString(grpc.getId()),
        grpc.getTitle().isBlank() ? null : grpc.getTitle(),
        grpc.getDescription().isBlank() ? null : grpc.getDescription(),
        photo,
        new GeoJson(
            grpc.getCity().isBlank() ? null : grpc.getCity(),
            grpc.getCountryId().isBlank() ? null : UUID.fromString(grpc.getCountryId())
        )
    );
  }

  public static Museum toGrpc(MuseumJson museum) {
    Museum.Builder builder = Museum.newBuilder();

    if(museum.id() != null){
      builder.setId(museum.id().toString());
    }

    if (!StringUtils.isEmpty(museum.title())) {
      builder.setTitle(museum.title());
    }

    if (!StringUtils.isEmpty(museum.description())) {
      builder.setDescription(museum.description());
    }

    if (museum.photo() != null && !museum.photo().isEmpty()) {
      builder.setPhoto(ByteString.copyFrom(museum.photo().getBytes(StandardCharsets.UTF_8)));
    }

    if (museum.geo() != null) {
      if (!StringUtils.isEmpty(museum.geo().city())) {
        builder.setCity(museum.geo().city());
      }
      if (museum.geo().country() != null && museum.geo().country().id() != null) {
        builder.setCountryId(museum.geo().country().id().toString());
      }
    }

    return builder.build();
  }
  public static AddMuseumRequest toPostGrpc(MuseumJson json) {
    Museum museum = toGrpc(json);
    return AddMuseumRequest.newBuilder()
        .setTitle(museum.getTitle())
        .setDescription(museum.getDescription())
        .setPhoto(museum.getPhoto())
        .setCity(museum.getCity())
        .setCountryId(museum.getCountryId())
        .build();
  }

  public static @Nonnull RestPage<MuseumJson> fromGrpcPage(@Nonnull PageMuseum grpc, @Nonnull Pageable pageable) {
    List<MuseumJson> content = grpc.getMuseumsList()
            .stream()
            .map(MuseumMapper::fromGrpc)
            .toList();

    return new RestPage<>(
            content,
            pageable,
            grpc.getTotalElements()
    );
  }
}
