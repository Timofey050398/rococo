package timofeyqa.rococo.jupiter.extension;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import timofeyqa.rococo.data.entity.Country;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.jupiter.annotation.Painting;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.model.rest.*;
import timofeyqa.rococo.service.ArtistClient;
import timofeyqa.rococo.service.CountryClient;
import timofeyqa.rococo.service.MuseumClient;
import timofeyqa.rococo.service.PaintingClient;
import timofeyqa.rococo.service.db.ArtistDbClient;
import timofeyqa.rococo.service.db.CountryDbClient;
import timofeyqa.rococo.service.db.MuseumDbClient;
import timofeyqa.rococo.service.db.PaintingDbClient;
import timofeyqa.rococo.utils.RandomDataUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static timofeyqa.rococo.jupiter.extension.ContentExtension.content;
import static timofeyqa.rococo.utils.PhotoConverter.loadImageAsString;
import static timofeyqa.rococo.utils.RandomDataUtils.*;

public class PaintingExtension implements BeforeEachCallback {

    private final MuseumClient museumClient = new MuseumDbClient();
    private final ArtistClient artistClient = new ArtistDbClient();
    private final PaintingClient paintingClient = new PaintingDbClient();
    private final CountryClient countryClient = new CountryDbClient();

    @Override
    public synchronized void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Content.class)
                .ifPresent(content -> {
                  final List<PaintingJson> createdPaintings = new ArrayList<>();
                  final ContentJson contentJson = content();
                  if (ArrayUtils.isNotEmpty(content.paintings())) {

                    for (Painting paintingAnno : content.paintings()) {
                      final String title = "".equals(paintingAnno.title())
                          ? randomPaintingTitle()
                          : paintingAnno.title();

                      PaintingJson paintingJson = paintingClient.findByTitle(title)
                          .orElseGet(() -> {
                            final String description = "".equals(paintingAnno.description())
                                ? randomDescription()
                                : paintingAnno.description();

                            final String photo = "".equals(paintingAnno.content())
                                ? null
                                : loadImageAsString(paintingAnno.content());

                            String artistName = StringUtils.isEmpty(paintingAnno.artist())
                                ? randomName()
                                : paintingAnno.artist();

                            //Артист обязателен
                            ArtistJson artist = contentJson.artists()
                                .stream()
                                // Сначала ищем артиста в приложении
                                .filter(artistJson -> artistJson.name().equals(artistName))
                                .findFirst()
                                // Если нет - ищем в базе
                                .orElseGet(() -> artistClient.findByName(artistName)
                                    // Если нет - создаём
                                    .orElseGet(()-> artistClient.create(new ArtistJson(
                                        null,
                                        artistName,
                                        randomDescription(),
                                        null,
                                        new HashSet<>()
                                    )))
                                );

                            //Музей не обязателен
                            MuseumJson museumJson;
                            if(!StringUtils.isEmpty(paintingAnno.museum())) {
                              String museumName = paintingAnno.museum();
                              museumJson = contentJson.museums()
                                  .stream()
                                  // Сначала ищем музей в приложении
                                  .filter(museum -> museum.title().equals(museumName))
                                  .findFirst()
                                  // Если нет - ищем в базе
                                  .orElseGet(() -> museumClient.findByTitle(museumName)
                                      // Если нет - создаём
                                      .orElseGet(()-> museumClient.create(new MuseumJson(
                                          null,
                                          museumName,
                                          RandomDataUtils.randomDescription(),
                                          null,
                                          new GeoJson(
                                              RandomDataUtils.randomCity(),
                                              countryClient.getByName(Country.random())
                                                  .orElseThrow()
                                          ),
                                          new HashSet<>()
                                      )))
                                  );
                            } else {
                              museumJson = null;
                            }

                            return paintingClient.create(
                                new PaintingJson(
                                    null,
                                    title,
                                    description,
                                    artist,
                                    museumJson,
                                    photo
                                )
                            );
                          });

                      contentJson.artists().stream()
                          .filter(artistJson -> artistJson.name().equals(paintingJson.artist().name()))
                          .findFirst()
                          .ifPresent(artistJson -> artistJson.paintings().add(paintingJson));

                      if(!StringUtils.isEmpty(paintingAnno.museum())){
                        contentJson.museums().stream()
                            .filter(museumJson1 -> museumJson1.title().equals(paintingJson.museum().title()))
                            .findFirst()
                            .ifPresent(museumJson1 -> museumJson1.paintings().add(paintingJson));
                      }

                      createdPaintings.add(paintingJson);
                    }
                  }

                  for (int i = 0; i < content.paintingCount(); i++) {
                    createdPaintings
                        .add(paintingClient.create(new PaintingJson(
                            null,
                            randomName(),
                            randomDescription(),
                            artistClient.create(new ArtistJson(
                                null,
                                randomName(),
                                randomDescription(),
                                null,
                                new HashSet<>()
                            )),
                            null,
                            loadImageAsString(randomFilePath("paintings"))
                        )));
                  }

                  contentJson.paintings().addAll(createdPaintings);
                });
    }
}
