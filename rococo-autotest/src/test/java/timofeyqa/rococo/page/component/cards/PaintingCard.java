package timofeyqa.rococo.page.component.cards;

import io.qameta.allure.Step;
import timofeyqa.rococo.page.detail.PaintingDetailPage;

import static com.codeborne.selenide.Condition.visible;

public class PaintingCard extends Card<PaintingCard> {
  private final static String TITLE_TAG = "div";

  public PaintingCard(String title) {
    super(title,TITLE_TAG);
  }

  @Override
  String titleTag() {
    return TITLE_TAG;
  }

  @Step("Open painting {title} card")
  @Override
  public PaintingDetailPage openDetail() {
    self
        .shouldBe(visible)
        .click();
    return new PaintingDetailPage();
  }
}
