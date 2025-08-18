package timofeyqa.rococo.page.component.cards;

import java.awt.image.BufferedImage;

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
}
