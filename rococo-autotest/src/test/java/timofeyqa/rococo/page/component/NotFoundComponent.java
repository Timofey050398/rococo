package timofeyqa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class NotFoundComponent extends BaseComponent<NotFoundComponent> {

  private final SelenideElement searchIcon = self.$("img");
  private final SelenideElement headerText = self.$("p.text-xl");
  private final SelenideElement bodyText = self.$("p.text-l");


  public NotFoundComponent() {
    super($("div.m-20.text-center"));
  }


  @Step("Compare not found component should shown")
  public void shouldShown(String xlText, String text){
    self.shouldBe(visible);
    searchIcon.shouldHave(attribute("alt","Иконка поиска"));
    headerText.shouldHave(text(xlText));
    bodyText.shouldHave(text(text));
  }
}
