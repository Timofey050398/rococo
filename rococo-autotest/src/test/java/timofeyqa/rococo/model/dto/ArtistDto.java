package timofeyqa.rococo.model.dto;

import lombok.Builder;
import lombok.ToString;
import timofeyqa.rococo.model.rest.ContentImpl;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@Builder(toBuilder=true)
public record ArtistDto(
    UUID id,
    String name,
    String biography,
    byte[]  photo,
    @ToString.Exclude
    Set<PaintingDto> paintings)  implements ContentImpl {

    public void compare(ArtistDto expected) {
        assertNotNull(expected);
        assertAll(
            () -> assertEquals(expected.id,id,"id not equals"),
            () -> assertEquals(expected.name,name,"name not equals"),
            () -> assertEquals(expected.biography,biography,"biography not equals"),
            () -> assertArrayEquals(expected.photo, photo,"photo's not equals")
        );
    }
}
