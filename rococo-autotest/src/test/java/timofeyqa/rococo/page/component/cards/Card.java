package timofeyqa.rococo.page.component.cards;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import timofeyqa.rococo.page.component.BaseComponent;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;
import static timofeyqa.rococo.condition.ScreenshotCondition.image;

public abstract class Card<T extends Card<?>> extends BaseComponent<T> {

  @Getter
  protected final SelenideElement image = self.$("img");

  protected final SelenideElement title = self.$x(String.format("./%s[0]",titleTag()));

  public static ElementsCollection cards() {
    return $$("ul li a");
  }

  protected Card(String title, String titleTag) {
    super($$("ul li a "+titleTag)
        .findBy(text(title))
        .parent()
    );
  }

  public Card(SelenideElement self) {
    super(self);
  }

  abstract String titleTag();

  @SuppressWarnings("unchecked")
  public T compareImage(BufferedImage expectedImage) {
    image.shouldBe(image(expectedImage));
    return (T) this;
  }

  public String getTitle(){
    return title.getText();
  }
}
