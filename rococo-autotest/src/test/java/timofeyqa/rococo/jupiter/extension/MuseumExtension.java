package timofeyqa.rococo.jupiter.extension;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import timofeyqa.rococo.data.entity.Country;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.jupiter.annotation.Museum;
import timofeyqa.rococo.model.rest.GeoJson;
import timofeyqa.rococo.model.dto.MuseumDto;
import timofeyqa.rococo.service.CountryClient;
import timofeyqa.rococo.service.MuseumClient;
import timofeyqa.rococo.service.db.CountryDbClient;
import timofeyqa.rococo.service.db.MuseumDbClient;
import timofeyqa.rococo.utils.RandomDataUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static timofeyqa.rococo.jupiter.extension.ContentExtension.content;
import static timofeyqa.rococo.utils.PhotoConverter.loadImageAsBytes;
import static timofeyqa.rococo.utils.RandomDataUtils.*;

@ParametersAreNonnullByDefault
public class MuseumExtension implements BeforeEachCallback {

    private final MuseumClient museumClient = new MuseumDbClient();
    private final CountryClient countryClient = new CountryDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Content.class)
                .ifPresent(content -> {
                  final Set<MuseumDto> preparedMuseums = new HashSet<>();
                  final Set<MuseumDto> createdMuseums = new HashSet<>();
                  if (ArrayUtils.isNotEmpty(content.museums())) {

                    for (Museum museumAnno : content.museums()) {
                      final String title = "".equals(museumAnno.title())
                          ? RandomDataUtils.randomMuseumName()
                          : museumAnno.title();

                      Optional<MuseumDto> museumDtoOptional = museumClient.findByTitle(title);

                      if (museumDtoOptional.isPresent()) {
                        createdMuseums.add(museumDtoOptional.get());
                      } else {
                        final String description = "".equals(museumAnno.description())
                            ? RandomDataUtils.randomDescription()
                            : museumAnno.description();

                        final byte[] photo = "".equals(museumAnno.photo())
                            ? null
                            : loadImageAsBytes(museumAnno.photo());

                        final String city = "".equals(museumAnno.city())
                            ? null
                            :museumAnno.city();

                        MuseumDto museumDto = new MuseumDto(
                            null,
                            title,
                            description,
                            photo,
                            new GeoJson(
                                city,
                                countryClient.getByName(museumAnno.country())
                                    .orElseThrow()
                                ),
                            new HashSet<>()
                        );
                        preparedMuseums.add(museumDto);
                      }
                    }
                  }
                  for (int i = 0; i < content.museumCount(); i++) {
                    var country = Country.random();
                    var museum = new MuseumDto(
                        null,
                        randomMuseumName(),
                        randomDescription(),
                        randomImage("museums"),
                        new GeoJson(
                            randomFirstname(),
                            countryClient
                                .getByName(country)
                                .orElseThrow(() -> new IllegalStateException("Country not found: "+ country))
                        ),
                        new HashSet<>()
                    );
                    preparedMuseums.add(museum);
                  }
                  createdMuseums.addAll(addBatch(preparedMuseums));

                  content().museums().addAll(createdMuseums);
                });
    }

    private synchronized Set<MuseumDto> addBatch(Set<MuseumDto> preparedMuseums) {
      return preparedMuseums.stream()
          .map(museumClient::create)
          .collect(Collectors.toSet());
    }
}
