package timofeyqa.rococo.page.lists;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import timofeyqa.rococo.page.component.forms.ArtistForm;
import timofeyqa.rococo.page.component.forms.PaintingForm;
import timofeyqa.rococo.page.detail.ArtistDetailPage;
import timofeyqa.rococo.page.BasePage;
import timofeyqa.rococo.page.component.*;
import timofeyqa.rococo.page.component.cards.ArtistCard;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class ArtistPage extends BasePage<ArtistPage> implements ListPage<ArtistPage> {
  public static final String URL = CFG.frontUrl()+"artist";
  @Getter
  protected final Header header = new Header();
  private final Title title = new Title();
  private final SelenideElement addArtistButton = $(byText("Добавить художника"));
  private final NotFoundComponent notFoundComponent = new NotFoundComponent();
  private final SelenideElement emptyPageText = $(byText("Пока что список художников пуст. Чтобы пополнить коллекцию, добавьте нового художника"));
  @Getter
  private final SearchBar searchBar = new SearchBar();

  public int pageSize(){
    return 18;
  }

  public ElementsCollection cards() {
    return ArtistCard.cards();
  }

  public ArtistPage search(String title){
    searchBar.search(title);
    return this;
  }

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

  public ArtistCard getCard(String name){
    searchBar.search(name);
    var card = new ArtistCard(name);
    card.visible();
    return card;
  }

  @Step("Click add artist button")
  public ArtistForm addArtist() {
    addArtistButton.click();
    return new ArtistForm();
  }

  @Override
  @Step("Ensure that the 'Page is empty' screen is shown")
  public ArtistPage comparePageIsEmpty(){
    cards().shouldHave(size(0));
    emptyPageText.shouldBe(visible);
    return this;
  }

  @Override
  @Step("Ensure that the 'No Card Found' screen is shown")
  public ArtistPage compareCardNotFound(){
    cards().shouldHave(size(0));
    notFoundComponent.shouldShown(
        "Художники не найдены",
        "Для указанного вами фильтра мы не смогли найти художников"
    );
    return this;
  }
}
