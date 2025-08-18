package timofeyqa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import timofeyqa.rococo.page.component.Header;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class NotFoundPage extends BasePage<NotFoundPage> {
  protected final Header header = new Header();
  private final SelenideElement mainPageButton = $(byText("На главную страницу"));
  private final SelenideElement title = $(byText("Страница не найдена"));


  @Override
  public NotFoundPage checkThatPageLoaded() {
    header.getSelf().should(visible)
        .shouldHave(text("Ro"))
        .shouldHave(text("coco"));
    mainPageButton.should(clickable);
    title.should(visible);
    return this;
  }

  public MainPage clickMainPageButton() {
    mainPageButton.should(clickable).click();
    return new MainPage();
  }
}
