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
import timofeyqa.rococo.model.dto.ArtistDto;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.model.dto.PaintingDto;
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

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static timofeyqa.rococo.jupiter.extension.ContentExtension.content;
import static timofeyqa.rococo.utils.PhotoConverter.loadImageAsBytes;
import static timofeyqa.rococo.utils.RandomDataUtils.*;

@ParametersAreNonnullByDefault
public class PaintingExtension implements BeforeEachCallback {

  private final MuseumClient museumClient = new MuseumDbClient();
  private final ArtistClient artistClient = new ArtistDbClient();
  private final PaintingClient paintingClient = new PaintingDbClient();
  private final CountryClient countryClient = new CountryDbClient();

  @Override
  public synchronized void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Content.class)
        .ifPresent(content -> {
          final List<PaintingDto> createdPaintings = new ArrayList<>();
          final ContentJson contentDto = content();
          if (ArrayUtils.isNotEmpty(content.paintings())) {

            for (Painting paintingAnno : content.paintings()) {
              final String title = "".equals(paintingAnno.title())
                  ? randomPaintingTitle()
                  : paintingAnno.title();

              PaintingDto paintingDto = paintingClient.findByTitle(title)
                  .orElseGet(() -> {
                    final String description = "".equals(paintingAnno.description())
                        ? randomDescription()
                        : paintingAnno.description();

                    final byte[] photo = "".equals(paintingAnno.content())
                        ? null
                        : loadImageAsBytes(paintingAnno.content());

                    String artistName = StringUtils.isEmpty(paintingAnno.artist())
                        ? randomName()
                        : paintingAnno.artist();

                    //Артист обязателен
                    ArtistDto artist = contentDto.artists()
                        .stream()
                        // Сначала ищем артиста в приложении
                        .filter(artistDto -> artistDto.name().equals(artistName))
                        .findFirst()
                        // Если нет - ищем в базе
                        .orElseGet(() -> artistClient.findByName(artistName)
                            // Если нет - создаём
                            .orElseGet(()-> randomArtist(artistName))
                        );

                    //Музей не обязателен
                    MuseumDto museumDto;
                    if (!StringUtils.isEmpty(paintingAnno.museum())) {
                      String museumName = paintingAnno.museum();
                      museumDto = contentDto.museums().stream()
                          // Сначала ищем музей в приложении
                          .filter(museum -> museum.title().equals(museumName))
                          .findFirst()
                          // Если нет - ищем в базе
                          .orElseGet(() -> museumClient.findByTitle(museumName)
                              // Если нет - создаём
                              .orElseGet(()-> randomMuseum(museumName))
                          );
                    } else {
                      museumDto = null;
                    }

                    return paintingClient.create(
                        new PaintingDto(
                            null,
                            title,
                            description,
                            artist,
                            museumDto,
                            photo
                        )
                    );
                  });

              contentDto.artists().stream()
                  .filter(artistDto -> artistDto.name().equals(paintingDto.artist().name()))
                  .findFirst()
                  .ifPresent(artistDto -> artistDto.paintings().add(paintingDto));

              if(!StringUtils.isEmpty(paintingAnno.museum())){
                contentDto.museums().stream()
                    .filter(museumDto1 -> museumDto1.title().equals(paintingDto.museum().title()))
                    .findFirst()
                    .ifPresent(museumDto1 -> museumDto1.paintings().add(paintingDto));
              }

              createdPaintings.add(paintingDto);
            }
          }

          ArtistDto artist = null;
          if (content.paintingCount() > 0) {
            artist = contentDto.artists().stream()
                .findFirst()
                .orElseGet(() -> {
                  ArtistDto newArtist = randomArtist(randomName());
                  contentDto.artists().add(newArtist);
                  return newArtist;
                });
          }

          for (int i = 0; i < content.paintingCount(); i++) {
            PaintingDto paintingDto = randomPainting(artist);
            artist.paintings().add(paintingDto);
            createdPaintings.add(paintingDto);
          }

          contentDto.paintings().addAll(createdPaintings);
        });
  }

  private PaintingDto randomPainting(ArtistDto artist) {
    return paintingClient.create(new PaintingDto(
        null,
        randomName(),
        randomDescription(),
        artist,
        null,
        randomImage("paintings")
    ));
  }

  private MuseumDto randomMuseum(String museumName) {
    return museumClient.create(new MuseumDto(
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
    ));
  }

  private ArtistDto randomArtist(final String name) {
      return artistClient.create(new ArtistDto(
          null,
          name,
          randomDescription(),
          null,
          new HashSet<>()
      ));
    }
}
