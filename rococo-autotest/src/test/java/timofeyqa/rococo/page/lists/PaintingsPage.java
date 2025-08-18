package timofeyqa.rococo.page.lists;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import timofeyqa.rococo.page.BasePage;
import timofeyqa.rococo.page.detail.PaintingDetailPage;
import timofeyqa.rococo.page.component.Header;
import timofeyqa.rococo.page.component.cards.PaintingCard;
import timofeyqa.rococo.page.component.SearchBar;
import timofeyqa.rococo.page.component.Title;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class PaintingsPage extends BasePage<PaintingsPage> {

  public static final String URL = CFG.frontUrl()+"painting";
  protected final Header header = new Header();
  private final Title title = new Title();
  private final SelenideElement addPaintingButton = $(byText("Добавить картину"));

  @Getter
  private final SearchBar searchBar = new SearchBar();
  private final ElementsCollection cards = PaintingCard.cards();

  @Override
  public PaintingsPage checkThatPageLoaded() {
    header.getSelf().should(visible)
        .shouldHave(text("Ro"))
        .shouldHave(text("coco"));
    searchBar
        .getSelf().shouldBe(visible);
    searchBar.comparePlaceholder("Искать картины...");
    title.compareText("Картины");
    return this;
  }

  @Step("Open painting {title} card")
  public PaintingDetailPage openDetail(String title) {
    new PaintingCard(title)
        .getSelf()
        .shouldBe(visible)
        .click();
    return new PaintingDetailPage();
  }
}
