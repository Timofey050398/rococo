package timofeyqa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import timofeyqa.rococo.page.component.Header;
import timofeyqa.rococo.page.component.cards.MainPageCard;
import timofeyqa.rococo.page.lists.ArtistPage;
import timofeyqa.rococo.page.lists.MuseumPage;
import timofeyqa.rococo.page.lists.PaintingsPage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage<MainPage> {
  public static final String URL = CFG.frontUrl();
  protected final Header header = new Header();
  private final MainPageCard paintings = new MainPageCard("Картины");
  private final MainPageCard artists = new MainPageCard("Художники");
  private final MainPageCard museums = new MainPageCard("Музеи");
  public final SelenideElement title = $("main nav p.text-3xl");

  @Override
  public MainPage checkThatPageLoaded() {
    header.getSelf().should(visible)
        .shouldHave(text("Ro"))
        .shouldHave(text("coco"));
    paintings.getSelf().shouldBe(clickable);
    artists.getSelf().shouldBe(clickable);
    museums.getSelf().shouldBe(clickable);
    title.shouldHave(text("Ваши любимые картины и художники всегда рядом"));
    return this;
  }

  public PaintingsPage clickPaintingsCard() {
    paintings.getSelf().click();
    return new PaintingsPage();
  }

  public ArtistPage clickArtistsCard() {
    artists.getSelf().click();
    return new ArtistPage();
  }

  public MuseumPage clickMuseumsCard() {
    museums.getSelf().click();
    return new MuseumPage();
  }
}
