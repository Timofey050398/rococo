package timofeyqa.rococo.jupiter.extension;

import timofeyqa.rococo.jupiter.annotation.Artist;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.model.rest.ArtistJson;
import timofeyqa.rococo.service.ArtistClient;
import timofeyqa.rococo.service.db.ArtistDbClient;
import timofeyqa.rococo.utils.RandomDataUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.*;

import static timofeyqa.rococo.jupiter.extension.ContentExtension.content;
import static timofeyqa.rococo.utils.PhotoConverter.loadImageAsString;
import static timofeyqa.rococo.utils.RandomDataUtils.randomFilePath;

public class ArtistExtension implements BeforeEachCallback {

    private final ArtistClient artistClient = new ArtistDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Content.class)
                .ifPresent(content -> {
                  final Set<ArtistJson> preparedArtists = new HashSet<>();
                  final Set<ArtistJson> createdArtists = new HashSet<>();
                    if (ArrayUtils.isNotEmpty(content.artists())) {

                        for (Artist artistAnno : content.artists()) {
                          final String name = "".equals(artistAnno.name())
                              ? RandomDataUtils.randomName()
                              : artistAnno.name();

                          Optional<ArtistJson> artist = artistClient.findByName(name);

                          if (artist.isPresent()) {
                            createdArtists.add(artist.get());
                          } else {
                            final String biography = "".equals(artistAnno.biography())
                                ? RandomDataUtils.randomDescription()
                                : artistAnno.biography();

                            final String photo = "".equals(artistAnno.photo())
                                ? null
                                : loadImageAsString(artistAnno.photo());

                            ArtistJson artistJson = new ArtistJson(
                                null,
                                name,
                                biography,
                                photo,
                                new HashSet<>()
                            );

                            preparedArtists.add(artistJson);
                          }
                        }
                    }
                  for (int i = 0; i < content.artistCount(); i++) {
                    preparedArtists
                        .add(new ArtistJson(
                            null,
                            RandomDataUtils.randomName(),
                            RandomDataUtils.randomDescription(),
                            loadImageAsString(randomFilePath("artists")),
                            new HashSet<>()
                        ));
                  }

                  createdArtists.addAll(addBatch(preparedArtists));

                  content().artists().addAll(createdArtists);
                });
    }


  private synchronized Set<ArtistJson> addBatch(Set<ArtistJson> preparedArtists) {
    final Set<ArtistJson> createdArtists = new HashSet<>();
    for (ArtistJson museumJson : preparedArtists) {
      createdArtists.add(artistClient.create(museumJson));
    }
    return createdArtists;
  }
}
