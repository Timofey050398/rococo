package timofeyqa.rococo.page.component.cards;


import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import timofeyqa.rococo.data.entity.Country;
import timofeyqa.rococo.page.detail.MuseumDetailPage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class MuseumCard extends Card<MuseumCard> {
  private final static String TITLE_TAG = "div";
  private final SelenideElement geo = self.$x("./div[2]");

  public MuseumCard(String title) {
    super(title,TITLE_TAG);
  }

  @Override
  public String titleTag() {
    return TITLE_TAG;
  }

  @Step("Open painting {title} card")
  @Override
  public MuseumDetailPage openDetail() {
    self
        .shouldBe(visible)
        .click();
    return new MuseumDetailPage();
  }

  public MuseumCard compareGeo(Country country, String city){
    geo.shouldBe(visible)
        .shouldHave(text(city+", "+country.getName()));
    return this;
  }
}
