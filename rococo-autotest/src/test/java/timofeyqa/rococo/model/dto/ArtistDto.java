package timofeyqa.rococo.model.dto;

import io.qameta.allure.Param;
import io.qameta.allure.model.Parameter;
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
    @Param(mode = Parameter.Mode.MASKED)
    byte[]  photo,
    @ToString.Exclude
    @Param(mode = Parameter.Mode.HIDDEN)
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
