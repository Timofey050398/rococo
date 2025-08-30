package timofeyqa.rococo.page.component.cards;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import timofeyqa.rococo.page.component.BaseComponent;
import timofeyqa.rococo.page.detail.DetailPage;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static timofeyqa.rococo.condition.ScreenshotCondition.image;

@ParametersAreNonnullByDefault
public abstract class Card<T extends Card<?>> extends BaseComponent<T> {

  @Getter
  protected final SelenideElement image = self.$("img");

  protected final SelenideElement title = self.$x(String.format("./%s[0]",titleTag()));

  public static ElementsCollection cards() {
    return $$("ul.grid li a");
  }

  protected Card(String title, String titleTag) {
    super($$("ul.grid li a "+titleTag)
        .findBy(text(title))
        .parent()
    );
  }

  abstract String titleTag();

  @SuppressWarnings("unchecked")
  @Step("Compare image")
  public T compareImage(BufferedImage expectedImage) {
    image.shouldBe(image(expectedImage));
    return (T) this;
  }


  @Step("Get title text")
  public String getTitle(){
    return title.getText();
  }

  @SuppressWarnings("unchecked")
  @Step("Check that card is visible")
  public T visible(){
    self.shouldBe(visible, Duration.ofSeconds(10));
    Selenide.executeJavaScript(
        "arguments[0].scrollIntoView({ block: 'center', inline: 'nearest' });",
        self.toWebElement()
    );
    return (T) this;
  }

  public abstract DetailPage openDetail();
}
