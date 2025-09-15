package timofeyqa.rococo.jupiter.annotation.meta;

import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;
import timofeyqa.rococo.jupiter.extension.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({
    IssueExtension.class,
    StandExtension.class,
    AllureJunit5.class,
    UserExtension.class,
    ArtistExtension.class,
    MuseumExtension.class,
    PaintingExtension.class,
    ContentExtension.class
})
public @interface KafkaTest {
}
