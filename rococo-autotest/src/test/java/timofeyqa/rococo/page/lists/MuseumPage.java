package timofeyqa.rococo.page.lists;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import timofeyqa.rococo.page.BasePage;
import timofeyqa.rococo.page.component.NotFoundComponent;
import timofeyqa.rococo.page.component.cards.MuseumCard;
import timofeyqa.rococo.page.component.forms.MuseumForm;
import timofeyqa.rococo.page.detail.MuseumDetailPage;
import timofeyqa.rococo.page.component.Header;
import timofeyqa.rococo.page.component.SearchBar;
import timofeyqa.rococo.page.component.Title;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class MuseumPage extends BasePage<MuseumPage> implements ListPage<MuseumPage> {

  public static final String URL = CFG.frontUrl()+"museum";
  protected final Header header = new Header();
  private final Title title = new Title();
  private final SelenideElement addMuseumButton = $(byText("Добавить музей"));
  private final SelenideElement emptyMuseumText = $(byText("Пока что список музеев пуст. Чтобы пополнить коллекцию, добавьте новый музей"));
  private final NotFoundComponent notFoundComponent = new NotFoundComponent();

  @Getter
  private final SearchBar searchBar = new SearchBar();

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

  public MuseumCard getCard(String title){
    searchBar.search(title);
    var card = new MuseumCard(title);
    card.visible();
    return card;
  }

  @Step("Click add artist button")
  public MuseumForm addMuseum() {
    addMuseumButton.click();
    return new MuseumForm();
  }

  public MuseumPage search(String title){
    searchBar.search(title);
    return this;
  }

  public int pageSize(){
    return 4;
  }

  public ElementsCollection cards() {
    return MuseumCard.cards();
  }

  @Override
  @Step("Ensure that the 'Page is empty' screen is shown")
  public MuseumPage comparePageIsEmpty(){
    cards().shouldHave(size(0));
    emptyMuseumText.shouldBe(visible);
    return this;
  }

  @Override
  @Step("Ensure that the 'No Card Found' screen is shown")
  public MuseumPage compareCardNotFound(){
    cards().shouldHave(size(0));
    notFoundComponent.shouldShown(
        "Музеи не найдены",
        "Для указанного вами фильтра мы не смогли не найти ни одного музея"
    );
    return this;
  }
}
