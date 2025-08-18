package timofeyqa.rococo.page.lists;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import timofeyqa.rococo.page.detail.ArtistDetailPage;
import timofeyqa.rococo.page.BasePage;
import timofeyqa.rococo.page.component.*;
import timofeyqa.rococo.page.component.cards.ArtistCard;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class ArtistPage extends BasePage<ArtistPage> {
  public static final String URL = CFG.frontUrl()+"artist";
  protected final Header header = new Header();
  private final Title title = new Title();
  private final SelenideElement addArtistButton = $(byText("Добавить художника"));

  @Getter
  private final SearchBar searchBar = new SearchBar();
  private final ElementsCollection cards = ArtistCard.cards();

  @Override
  public ArtistPage checkThatPageLoaded() {
    header.getSelf().should(visible)
        .shouldHave(text("Ro"))
        .shouldHave(text("coco"));
    searchBar
        .getSelf().shouldBe(visible);
    searchBar.comparePlaceholder("Искать художников...");
    title.compareText("Художники");
    return this;
  }

  @Step("Open artist {name} card")
  public ArtistDetailPage openDetail(String name) {
    new ArtistCard(name)
        .getSelf()
        .shouldBe(visible)
        .click();
    return new ArtistDetailPage();
  }
}
