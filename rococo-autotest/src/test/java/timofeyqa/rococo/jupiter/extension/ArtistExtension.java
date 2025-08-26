package timofeyqa.rococo.jupiter.extension;

import timofeyqa.rococo.jupiter.annotation.Artist;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.model.dto.ArtistDto;
import timofeyqa.rococo.service.ArtistClient;
import timofeyqa.rococo.service.db.ArtistDbClient;
import timofeyqa.rococo.utils.RandomDataUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.*;

import static timofeyqa.rococo.jupiter.extension.ContentExtension.content;
import static timofeyqa.rococo.utils.PhotoConverter.loadImageAsBytes;
import static timofeyqa.rococo.utils.RandomDataUtils.*;

public class ArtistExtension implements BeforeEachCallback {

    private final ArtistClient artistClient = new ArtistDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Content.class)
                .ifPresent(content -> {
                  final Set<ArtistDto> preparedArtists = new HashSet<>();
                  final Set<ArtistDto> createdArtists = new HashSet<>();
                    if (ArrayUtils.isNotEmpty(content.artists())) {

                        for (Artist artistAnno : content.artists()) {
                          final String name = "".equals(artistAnno.name())
                              ? RandomDataUtils.randomName()
                              : artistAnno.name();

                          Optional<ArtistDto> artist = artistClient.findByName(name);

                          if (artist.isPresent()) {
                            createdArtists.add(artist.get());
                          } else {
                            final String biography = "".equals(artistAnno.biography())
                                ? randomDescription()
                                : artistAnno.biography();

                            final byte[] photo = "".equals(artistAnno.photo())
                                ? null
                                : loadImageAsBytes(artistAnno.photo());

                            ArtistDto artistDto = new ArtistDto(
                                null,
                                name,
                                biography,
                                photo,
                                new HashSet<>()
                            );

                            preparedArtists.add(artistDto);
                          }
                        }
                    }
                  for (int i = 0; i < content.artistCount(); i++) {
                    preparedArtists
                        .add(new ArtistDto(
                            null,
                            randomName(),
                            randomDescription(),
                            randomImage("artists"),
                            new HashSet<>()
                        ));
                  }

                  createdArtists.addAll(addBatch(preparedArtists));

                  content().artists().addAll(createdArtists);
                });
    }


  private synchronized Set<ArtistDto> addBatch(Set<ArtistDto> preparedArtists) {
    final Set<ArtistDto> createdArtists = new HashSet<>();
    for (ArtistDto museumDto : preparedArtists) {
      createdArtists.add(artistClient.create(museumDto));
    }
    return createdArtists;
  }
}
