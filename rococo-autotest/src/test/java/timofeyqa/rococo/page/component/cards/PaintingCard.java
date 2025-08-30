package timofeyqa.rococo.page.component.cards;

import io.qameta.allure.Step;
import timofeyqa.rococo.page.detail.PaintingDetailPage;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;

@ParametersAreNonnullByDefault
public class PaintingCard extends Card<PaintingCard> {
  private final static String TITLE_TAG = "div";

  public PaintingCard(String title) {
    super(title,TITLE_TAG);
  }

  @Override
  String titleTag() {
    return TITLE_TAG;
  }

  @Step("Open painting detail card")
  @Override
  public PaintingDetailPage openDetail() {
    self
        .shouldBe(visible)
        .click();
    return new PaintingDetailPage();
  }
}
