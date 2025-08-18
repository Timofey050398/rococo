package timofeyqa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;

public class SearchBar extends BaseComponent<SearchBar> {

  private final SelenideElement input = self.$("input[type='search']");
  private final SelenideElement searchButton = self.$("button");

  public SearchBar() {
    super($("div.flex.justify-center.mb-4.mx-8")); // корневой локатор
  }

  @Step("Type text {text} into search input")
  public SearchBar typeSearch(String text) {
    input.setValue(text);
    return this;
  }

  @Step("Click search icon")
  public SearchBar clickSearch() {
    searchButton.click();
    return this;
  }

  @Step("Search by text {text}")
  public SearchBar search(String text) {
    return typeSearch(text).clickSearch();
  }

  @Step("Compare that placeholder has text {expected}")
  public SearchBar comparePlaceholder(String expected) {
    input.shouldHave(attribute("placeholder", expected));
    return this;
  }
}
