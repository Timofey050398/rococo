package timofeyqa.rococo.page.detail;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import timofeyqa.rococo.page.BasePage;
import timofeyqa.rococo.page.component.Header;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class PaintingDetailPage extends BasePage<PaintingDetailPage> {

  @Getter
  protected final Header header = new Header();
  private final SelenideElement image = $("article div img");
  private final SelenideElement title = $("article header");
  private final SelenideElement artist = $("article div.text-center");
  private final SelenideElement description = $("article.card .grid > div:last-child .m-4");
  private final SelenideElement editPaintingButton = $("button[data-testid='edit-painting']");
  @Override
  public PaintingDetailPage checkThatPageLoaded() {
    header.getSelf().should(visible)
        .shouldHave(text("Ro"))
        .shouldHave(text("coco"));
    image.should(visible);
    title.should(visible);
    artist.should(visible);
    description.should(visible);
    return this;
  }

  public String url(String id){
    return CFG.frontUrl()+"painting/"+id;
  }
}
