package timofeyqa.rococo.model.dto;

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
    byte[] photo,
    GeoJson geo,
    @ToString.Exclude
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