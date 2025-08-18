package timofeyqa.rococo.page.detail;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import timofeyqa.rococo.page.BasePage;
import timofeyqa.rococo.page.component.Header;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class MuseumDetailPage extends BasePage<MuseumDetailPage> {

  @Getter
  protected final Header header = new Header();
  private final SelenideElement image = $("article div img");
  private final SelenideElement name = $("article header");
  private final SelenideElement geo = $("article div.text-center");
  private final SelenideElement description = $("article.card .grid > div > div:last-child");
  private final SelenideElement editMuseumButton = $("button[data-testid='edit-museum']");


  @Override
   public MuseumDetailPage checkThatPageLoaded() {
    header.getSelf().should(visible)
        .shouldHave(text("Ro"))
        .shouldHave(text("coco"));
    image.should(visible);
    name.should(visible);
    geo.should(visible);
    description.should(visible);
    return this;
  }

  public String url(String id){
    return CFG.frontUrl()+"museum/"+id;
  }
}
