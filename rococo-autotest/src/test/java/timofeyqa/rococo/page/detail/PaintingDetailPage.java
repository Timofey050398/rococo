package timofeyqa.rococo.page.detail;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import timofeyqa.rococo.page.BasePage;
import timofeyqa.rococo.page.component.Header;
import timofeyqa.rococo.page.component.forms.PaintingForm;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.util.UUID;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static timofeyqa.rococo.condition.ScreenshotCondition.image;

@ParametersAreNonnullByDefault
public class PaintingDetailPage extends BasePage<PaintingDetailPage> implements DetailPage {

  @Getter
  protected final Header header = new Header();
  private final SelenideElement paintingImage = $("article div img");
  private final SelenideElement title = $("article header");
  private final SelenideElement artist = $("article div.text-center");
  private final SelenideElement description = $("article.card .grid > div:last-child .m-4");
  private final SelenideElement editPaintingButton = $("button[data-testid='edit-painting']");
  @Override
  public PaintingDetailPage checkThatPageLoaded() {
    header.getSelf().should(visible)
        .shouldHave(text("Ro"))
        .shouldHave(text("coco"));
    title.should(visible);
    artist.should(visible);
    description.should(visible);
    return this;
  }

  @Step("Open edit painting form")
  public PaintingForm openEditForm(){
    editPaintingButton.shouldBe(visible).click();
    return new PaintingForm();
  }


  @Step("Compare detail page painting image")
  public PaintingDetailPage compareImage(BufferedImage expected) {
    paintingImage.shouldBe(image(expected));
    return this;
  }

  @Step("Compare that title is {title}")
  public PaintingDetailPage compareTitle(String title){
    this.title.shouldHave(text(title));
    return this;
  }

  @Step("Compare that description is {description}")
  public PaintingDetailPage compareDescription(String description){
    this.description.shouldHave(text(description));
    return this;
  }

  @Step("Compare that artist name is {artistName}")
  public PaintingDetailPage comapreArtist(String artistName){
    this.artist.shouldHave(text(artistName));
    return this;
  }

  public static String url(UUID id){
    return CFG.frontUrl()+"painting/"+id;
  }
}
