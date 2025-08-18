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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static timofeyqa.rococo.jupiter.extension.ContentExtension.content;
import static timofeyqa.rococo.utils.PhotoConverter.loadImageAsString;

public class ArtistExtension implements BeforeEachCallback {

    private final ArtistClient artistClient = new ArtistDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Content.class)
                .ifPresent(content -> {
                    if (ArrayUtils.isNotEmpty(content.artists())) {

                        final List<ArtistJson> createdArtists = new ArrayList<>();

                        for (Artist artistAnno : content.artists()) {
                            final String name = "".equals(artistAnno.name())
                                    ? RandomDataUtils.randomName()
                                    : artistAnno.name();

                            ArtistJson artistJson = artistClient.findByName(name)
                                .orElseGet(() -> {
                                  final String biography = "".equals(artistAnno.biography())
                                      ? RandomDataUtils.randomDescription()
                                      : artistAnno.biography();

                                  final String photo = "".equals(artistAnno.photo())
                                      ? null
                                      : loadImageAsString(artistAnno.photo());

                                  return artistClient.create(new ArtistJson(
                                      null,
                                      name,
                                      biography,
                                      photo,
                                      new HashSet<>()
                                  ));
                                });
                            createdArtists.add(artistJson);
                        }
                        content().artists().addAll(createdArtists);
                    }
                });
    }
}
