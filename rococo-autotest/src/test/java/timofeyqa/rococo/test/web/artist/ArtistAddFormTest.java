package timofeyqa.rococo.test.web.artist;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import timofeyqa.rococo.jupiter.annotation.*;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.ContentJson;
import timofeyqa.rococo.page.MainPage;

import java.util.Arrays;


@WebTest
public class ArtistAddFormTest {

  @BeforeEach
  public void setup(TestInfo testInfo) {
    System.out.println("Method annotations: " +
        Arrays.toString(testInfo.getTestMethod().orElseThrow().getAnnotations()));
  }

  @Test
  @Content(
      artists = {@Artist()},
      paintings = {@Painting()},
      museums = {@Museum()}
  )
  @User
  @ApiLogin
  public void artistAddFormTest(ContentJson content){
    System.out.println(content);
    Selenide.open(MainPage.URL, MainPage.class)
        .clickArtistsCard()
        .checkThatPageLoaded();
  }
}
