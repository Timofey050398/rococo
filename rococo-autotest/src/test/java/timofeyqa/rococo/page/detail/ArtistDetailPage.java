package timofeyqa.rococo.page.detail;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import timofeyqa.rococo.page.BasePage;
import timofeyqa.rococo.page.component.Header;
import timofeyqa.rococo.page.component.cards.PaintingCard;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class ArtistDetailPage extends BasePage<ArtistDetailPage> {

  @Getter
  protected final Header header = new Header();
  private final SelenideElement image = $("article div img");
  private final SelenideElement name = $("article header");
  private final SelenideElement biography = $("article p");
  private final ElementsCollection cards = PaintingCard.cards();
  private final SelenideElement addPaintingButton = $(byText("Добавить картину"));
  private final SelenideElement editArtistButton = $("button[data-testid='edit-artist']");

  @Override
  public ArtistDetailPage checkThatPageLoaded() {
    header.getSelf().should(visible)
        .shouldHave(text("Ro"))
        .shouldHave(text("coco"));
    image.should(visible);
    name.should(visible);
    biography.should(visible);
    return this;
  }

  public String url(String id){
    return CFG.frontUrl()+"painting/"+id;
  }
}
