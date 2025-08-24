package timofeyqa.rococo.page.lists;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import timofeyqa.rococo.page.BasePage;
import timofeyqa.rococo.page.component.NotFoundComponent;
import timofeyqa.rococo.page.component.cards.MuseumCard;
import timofeyqa.rococo.page.component.forms.PaintingForm;
import timofeyqa.rococo.page.component.Header;
import timofeyqa.rococo.page.component.cards.PaintingCard;
import timofeyqa.rococo.page.component.SearchBar;
import timofeyqa.rococo.page.component.Title;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class PaintingsPage extends BasePage<PaintingsPage> implements CardListPage<PaintingsPage, PaintingCard> {

  public static final String URL = CFG.frontUrl()+"painting";
  protected final Header header = new Header();
  private final Title title = new Title();
  private final SelenideElement addPaintingButton = $(byText("Добавить картину"));
  private final SelenideElement emptyPageText = $(byText("Пока что список картин пуст. Чтобы пополнить коллекцию, добавьте новую картину"));
  private final NotFoundComponent notFoundComponent = new NotFoundComponent();

  @Getter
  private final SearchBar searchBar = new SearchBar();

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

  @Step("Click add painting button")
  public PaintingForm addPainting() {
    addPaintingButton.click();
    return new PaintingForm();
  }

  @Override
  public PaintingCard getCard(String title){
    searchBar.search(title);
    var card = new PaintingCard(title);
    card.visible();
    return card;
  }

  public PaintingsPage search(String title){
    searchBar.search(title);
    return this;
  }

  public int pageSize(){
    return 9;
  }

  public ElementsCollection cards() {
    return PaintingCard.cards();
  }

  @Override
  @Step("Ensure that the 'Page is empty' screen is shown")
  public PaintingsPage comparePageIsEmpty(){
    cards().shouldHave(size(0));
    emptyPageText.shouldBe(visible);
    return this;
  }

  @Override
  @Step("Ensure that the 'No Card Found' screen is shown")
  public PaintingsPage compareCardNotFound(){
    cards().shouldHave(size(0));
    notFoundComponent.shouldShown(
        "Картины не найдены",
        "Для указанного вами фильтра мы не смогли не найти ни одной картины"
    );
    return this;
  }
}
