package timofeyqa.rococo.model.dto;

import io.qameta.allure.Param;
import io.qameta.allure.model.Parameter;
import lombok.Builder;
import lombok.ToString;
import timofeyqa.rococo.model.rest.ContentImpl;
import timofeyqa.rococo.model.rest.GeoJson;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Builder(toBuilder = true)
public record MuseumDto(
    UUID id,
    String title,
    String description,
    @Param(mode = Parameter.Mode.MASKED)
    byte[] photo,
    GeoJson geo,
    @ToString.Exclude
    @Param(mode = Parameter.Mode.HIDDEN)
    Set<PaintingDto> paintings) implements ContentImpl {

    public void compare(MuseumDto expected) {
        assertNotNull(expected);
        assertAll(
            () -> assertEquals(expected.id,id,"id not equals"),
            () -> assertEquals(expected.title,title,"title not equals"),
            () -> assertEquals(expected.description,description,"biography not equals"),
            () -> assertArrayEquals(expected.photo, photo,"photo's not equals"),
            () -> {
                if (geo != null) {
                    assertEquals(expected.geo.city(),geo.city(),"city not equals");
                } else {
                    assertNull(expected.geo,"geo not equals");
            }},
            () -> {
                if (geo != null && geo.country() != null) {
                    assertAll(
                        () -> assertEquals(expected.geo.country().id(),geo.country().id(),"country id not equals"),
                        () -> assertEquals(expected.geo.country().name(),geo.country().name(),"country name not equals")
                    );
                } else  {
                    assertNull(expected.geo,"geo not equals");
            }}
        );
    }
}