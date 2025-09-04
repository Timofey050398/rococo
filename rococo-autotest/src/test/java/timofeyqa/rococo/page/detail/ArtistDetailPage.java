package timofeyqa.rococo.page.detail;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import timofeyqa.rococo.page.BasePage;
import timofeyqa.rococo.page.component.Header;
import timofeyqa.rococo.page.component.cards.PaintingCard;
import timofeyqa.rococo.page.component.forms.ArtistForm;
import timofeyqa.rococo.page.component.forms.PaintingForm;
import timofeyqa.rococo.page.lists.CardListPage;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.util.UUID;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static timofeyqa.rococo.condition.ScreenshotCondition.image;

@ParametersAreNonnullByDefault
public class ArtistDetailPage extends BasePage<ArtistDetailPage> implements DetailPage, CardListPage<ArtistDetailPage, PaintingCard> {

  @Getter
  protected final Header header = new Header();
  private final SelenideElement artistImage = $("article div img");
  private final SelenideElement name = $("article header");
  private final SelenideElement biography = $("article p");
  private final SelenideElement addPaintingButton = $(byText("Добавить картину"));
  private final SelenideElement editArtistButton = $("button[data-testid='edit-artist']");
  private final SelenideElement detailCard = $("article.card");


  @Override
  @Step("Check that page is loaded")
  public ArtistDetailPage checkThatPageLoaded() {
    header.getSelf().should(visible)
        .shouldHave(text("Ro"))
        .shouldHave(text("coco"));
    name.should(visible);
    biography.should(visible);
    return this;
  }

  @Override
  @Step("Check that card is existed")
  public PaintingCard getCard(String title){
    var card = new PaintingCard(title);
    card.visible();
    return card;
  }

  @Override
  public int pageSize() {
    return 9;
  }

  @Override
  public ElementsCollection cards(){
    return PaintingCard.cards();
  }

  @Override
  @Step("Compare that page is empty")
  public ArtistDetailPage comparePageIsEmpty() {
    cards().shouldHave(size(0));
    detailCard.shouldHave(text("Пока что список картин этого художника пуст."));
    return this;
  }

  @Step("Compare that name is {name}")
  public ArtistDetailPage compareName(String name){
    this.name.shouldHave(text(name));
    return this;
  }

  @Step("Compare that biography is {biography}")
  public ArtistDetailPage compareBiography(String biography){
    this.biography.shouldHave(text(biography));
    return this;
  }

  @Step("Compare detail page museum image")
  public ArtistDetailPage compareImage(BufferedImage expected) {
    artistImage.shouldBe(image(expected));
    return this;
  }

  @Step("Open edit artist form")
  public ArtistForm openEditForm(){
    editArtistButton.shouldBe(visible).click();
    return new ArtistForm();
  }

  @Step("Open add painting form at artist detail")
  public PaintingForm openPaintingForm(){
    addPaintingButton.shouldBe(visible).click();
    return new PaintingForm();
  }

  @Override
  public ArtistDetailPage compareCardNotFound() {
    throw new UnsupportedOperationException("Not supported yet.");
  }


  @Step("Get url for artist detail {id}")
  public static String url(UUID id){
    return CFG.frontUrl()+"artist/"+id;
  }
}
