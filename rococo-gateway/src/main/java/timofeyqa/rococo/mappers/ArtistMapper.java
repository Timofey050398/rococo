package timofeyqa.rococo.mappers;

import com.google.protobuf.ByteString;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import timofeyqa.grpc.rococo.AddArtistRequest;
import timofeyqa.grpc.rococo.Artist;
import timofeyqa.grpc.rococo.PageArtistResponse;
import timofeyqa.rococo.model.ArtistJson;
import timofeyqa.rococo.model.page.RestPage;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component
public class ArtistMapper {

  public static @Nonnull ArtistJson fromGrpc(Artist grpc) {
    final String photo = grpc.getPhoto().isEmpty()
            ? null
            : new String(grpc.getPhoto().toByteArray(), StandardCharsets.UTF_8);
    return new ArtistJson(
        grpc.getId().isBlank() ? null : UUID.fromString(grpc.getId()),
        grpc.getName().isBlank() ? null : grpc.getName(),
        grpc.getBiography().isBlank() ? null : grpc.getBiography(),
        photo
    );
  }

  public static Artist toGrpc(ArtistJson artistJson) {
        Artist.Builder builder = Artist.newBuilder();

        if(artistJson.id() != null){
          builder.setId(artistJson.id().toString());
        }

        if (!StringUtils.isEmpty(artistJson.name())) {
            builder.setName(artistJson.name());
        }

        if (!StringUtils.isEmpty(artistJson.biography())) {
            builder.setBiography(artistJson.biography());
        }

        if (artistJson.photo() != null && !artistJson.photo().isEmpty()) {
            builder.setPhoto(ByteString.copyFrom(artistJson.photo().getBytes(StandardCharsets.UTF_8)));
        }

        return builder.build();
  }

  public static AddArtistRequest toPostGrpc(ArtistJson artistJson) {
    Artist artist = toGrpc(artistJson);
    return AddArtistRequest.newBuilder()
        .setName(artist.getName())
        .setBiography(artist.getBiography())
        .setPhoto(artist.getPhoto())
        .build();
  }

  public static @Nonnull RestPage<ArtistJson> fromGrpcPage(@Nonnull PageArtistResponse grpc, @Nonnull Pageable pageable) {
    List<ArtistJson> content = grpc.getArtistsList()
            .stream()
            .map(ArtistMapper::fromGrpc)
            .toList();

    return new RestPage<>(
            content,
            pageable,
            grpc.getTotalElements()
    );
  }
}
