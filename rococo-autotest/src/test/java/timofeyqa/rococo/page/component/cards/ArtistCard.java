package timofeyqa.rococo.page.component.cards;

import io.qameta.allure.Step;
import timofeyqa.rococo.page.detail.ArtistDetailPage;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.visible;
import static timofeyqa.rococo.condition.ScreenshotCondition.image;

public class ArtistCard extends Card<ArtistCard> {
  private final static String TITLE_TAG = "span";

  public ArtistCard(String name) {
    super(name,TITLE_TAG);
  }

  @Override
  public String titleTag() {
    return TITLE_TAG;
  }

  @Override
  public ArtistCard compareImage(BufferedImage expectedImage) {
    self.$("figure").shouldBe(image(expectedImage));
    return this;
  }

  @Step("Open painting {title} card")
  @Override
  public ArtistDetailPage openDetail() {
    self
        .shouldBe(visible)
        .click();
    return new ArtistDetailPage();
  }
}
