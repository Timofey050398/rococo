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

import java.awt.image.BufferedImage;
import java.util.UUID;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static timofeyqa.rococo.condition.ScreenshotCondition.image;

public class ArtistDetailPage extends BasePage<ArtistDetailPage> implements DetailPage {

  @Getter
  protected final Header header = new Header();
  private final SelenideElement artistImage = $("article div img");
  private final SelenideElement name = $("article header");
  private final SelenideElement biography = $("article p");
  private final SelenideElement addPaintingButton = $(byText("Добавить картину"));
  private final SelenideElement editArtistButton = $("button[data-testid='edit-artist']");

  @Override
  public ArtistDetailPage checkThatPageLoaded() {
    header.getSelf().should(visible)
        .shouldHave(text("Ro"))
        .shouldHave(text("coco"));
    name.should(visible);
    biography.should(visible);
    return this;
  }

  public ElementsCollection cards(){
    return PaintingCard.cards();
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

  public static String url(UUID id){
    return CFG.frontUrl()+"artist/"+id;
  }
}
