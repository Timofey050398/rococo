package timofeyqa.rococo.page.lists;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import timofeyqa.rococo.page.BasePage;
import timofeyqa.rococo.page.detail.MuseumDetailPage;
import timofeyqa.rococo.page.component.Header;
import timofeyqa.rococo.page.component.cards.MuseumCard;
import timofeyqa.rococo.page.component.SearchBar;
import timofeyqa.rococo.page.component.Title;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class MuseumPage extends BasePage<MuseumPage> {
  public static final String URL = CFG.frontUrl()+"museum";
  protected final Header header = new Header();
  private final Title title = new Title();
  private final SelenideElement addMuseumButton = $(byText("Добавить музей"));

  @Getter
  private final SearchBar searchBar = new SearchBar();
  private final ElementsCollection cards = MuseumCard.cards();

  @Override
  public MuseumPage checkThatPageLoaded() {
    header.getSelf().should(visible)
        .shouldHave(text("Ro"))
        .shouldHave(text("coco"));
    searchBar
        .getSelf().shouldBe(visible);
    searchBar.comparePlaceholder("Искать музей...");
    title.compareText("Музеи");
    return this;
  }

  @Step("Open museum {title} card")
  public MuseumDetailPage openDetail(String title) {
    new MuseumCard(title)
        .getSelf()
        .shouldBe(visible)
        .click();
    return new MuseumDetailPage();
  }
}
