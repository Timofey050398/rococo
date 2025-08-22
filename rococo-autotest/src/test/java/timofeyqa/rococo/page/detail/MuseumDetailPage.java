package timofeyqa.rococo.page.detail;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import timofeyqa.rococo.page.BasePage;
import timofeyqa.rococo.page.component.Header;
import timofeyqa.rococo.page.component.forms.PaintingForm;

import java.awt.image.BufferedImage;
import java.util.UUID;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static timofeyqa.rococo.condition.ScreenshotCondition.image;

public class MuseumDetailPage extends BasePage<MuseumDetailPage> implements DetailPage {

  @Getter
  protected final Header header = new Header();
  private final SelenideElement museumImage = $("article div img");
  private final SelenideElement name = $("article header");
  private final SelenideElement geo = $("article div.text-center");
  private final SelenideElement description = $("article.card .grid > div > div:last-child");
  private final SelenideElement editMuseumButton = $("button[data-testid='edit-museum']");


  @Override
   public MuseumDetailPage checkThatPageLoaded() {
    header.getSelf().should(visible)
        .shouldHave(text("Ro"))
        .shouldHave(text("coco"));
    name.should(visible);
    geo.should(visible);
    description.should(visible);
    return this;
  }

  @Step("Open edit museum form")
  public PaintingForm openEditForm(){
    editMuseumButton.shouldBe(visible).click();
    return new PaintingForm();
  }

  @Step("Compare detail page museum image")
  public MuseumDetailPage compareImage(BufferedImage expected) {
    museumImage.shouldBe(image(expected));
    return this;
  }

  public static String url(UUID id){
    return CFG.frontUrl()+"museum/"+id;
  }
}
