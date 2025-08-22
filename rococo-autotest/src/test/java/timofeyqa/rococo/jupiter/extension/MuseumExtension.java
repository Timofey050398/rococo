package timofeyqa.rococo.jupiter.extension;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import timofeyqa.rococo.data.entity.Country;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.jupiter.annotation.Museum;
import timofeyqa.rococo.model.rest.ArtistJson;
import timofeyqa.rococo.model.rest.CountryJson;
import timofeyqa.rococo.model.rest.GeoJson;
import timofeyqa.rococo.model.rest.MuseumJson;
import timofeyqa.rococo.service.CountryClient;
import timofeyqa.rococo.service.MuseumClient;
import timofeyqa.rococo.service.db.CountryDbClient;
import timofeyqa.rococo.service.db.MuseumDbClient;
import timofeyqa.rococo.utils.RandomDataUtils;

import java.util.*;

import static timofeyqa.rococo.jupiter.extension.ContentExtension.content;
import static timofeyqa.rococo.utils.PhotoConverter.loadImageAsString;
import static timofeyqa.rococo.utils.RandomDataUtils.*;

public class MuseumExtension implements BeforeEachCallback {

    private final MuseumClient museumClient = new MuseumDbClient();
    private final CountryClient countryClient = new CountryDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Content.class)
                .ifPresent(content -> {
                  final Set<MuseumJson> preparedMuseums = new HashSet<>();
                  final Set<MuseumJson> createdMuseums = new HashSet<>();
                  if (ArrayUtils.isNotEmpty(content.museums())) {

                    for (Museum museumAnno : content.museums()) {
                      final String title = "".equals(museumAnno.title())
                          ? RandomDataUtils.randomMuseumName()
                          : museumAnno.title();

                      Optional<MuseumJson> museumJsonOptional = museumClient.findByTitle(title);

                      if (museumJsonOptional.isPresent()) {
                        createdMuseums.add(museumJsonOptional.get());
                      } else {
                        final String description = "".equals(museumAnno.description())
                            ? RandomDataUtils.randomDescription()
                            : museumAnno.description();

                        final String photo = "".equals(museumAnno.photo())
                            ? null
                            : loadImageAsString(museumAnno.photo());

                        final String city = "".equals(museumAnno.city())
                            ? null
                            :museumAnno.city();

                        final CountryJson country = countryClient.getByName(
                            museumAnno.country()
                        ).orElseThrow();

                        MuseumJson museumJson = new MuseumJson(
                            null,
                            title,
                            description,
                            photo,
                            new GeoJson(city, country),
                            new HashSet<>()
                        );
                        preparedMuseums.add(museumJson);
                      }
                    }
                  }
                  for (int i = 0; i < content.museumCount(); i++) {
                    preparedMuseums
                        .add(new MuseumJson(
                            null,
                            randomMuseumName(),
                            RandomDataUtils.randomDescription(),
                            loadImageAsString(randomFilePath("museums")),
                            new GeoJson(
                                randomFirstname(),
                                countryClient.getByName(Country.random())
                                    .orElseThrow()
                            ),
                            new HashSet<>()
                        ));
                  }
                  createdMuseums.addAll(addBatch(preparedMuseums));

                  content().museums().addAll(createdMuseums);
                });
    }

    private synchronized Set<MuseumJson> addBatch(Set<MuseumJson> preparedMuseums) {
      final Set<MuseumJson> createdMuseums = new HashSet<>();
      for (MuseumJson museumJson : preparedMuseums) {
        createdMuseums.add(museumClient.create(museumJson));
      }
      return createdMuseums;
    }
}
