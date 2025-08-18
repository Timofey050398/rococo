package timofeyqa.rococo.jupiter.extension;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import timofeyqa.rococo.jupiter.annotation.Content;
import timofeyqa.rococo.jupiter.annotation.Museum;
import timofeyqa.rococo.model.rest.CountryJson;
import timofeyqa.rococo.model.rest.GeoJson;
import timofeyqa.rococo.model.rest.MuseumJson;
import timofeyqa.rococo.service.CountryClient;
import timofeyqa.rococo.service.MuseumClient;
import timofeyqa.rococo.service.db.CountryDbClient;
import timofeyqa.rococo.service.db.MuseumDbClient;
import timofeyqa.rococo.utils.RandomDataUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static timofeyqa.rococo.jupiter.extension.ContentExtension.content;
import static timofeyqa.rococo.utils.PhotoConverter.loadImageAsString;

public class MuseumExtension implements BeforeEachCallback {

    private final MuseumClient museumClient = new MuseumDbClient();
    private final CountryClient countryClient = new CountryDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Content.class)
                .ifPresent(content -> {
                    if (ArrayUtils.isNotEmpty(content.museums())) {

                        final List<MuseumJson> createdMuseums = new ArrayList<>();

                        for (Museum museumAnno : content.museums()) {
                            final String title = "".equals(museumAnno.title())
                                    ? RandomDataUtils.randomMuseumName()
                                    : museumAnno.title();

                            MuseumJson museumJson = museumClient.findByTitle(title)
                                .orElseGet(() -> {
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

                                    return museumClient.create(new MuseumJson(
                                        null,
                                        title,
                                        description,
                                        photo,
                                        new GeoJson(city, country),
                                        new HashSet<>()
                                    ));
                                });

                            createdMuseums.add(museumJson);
                        }
                        content().museums().addAll(createdMuseums);
                    }
                });
    }
}
