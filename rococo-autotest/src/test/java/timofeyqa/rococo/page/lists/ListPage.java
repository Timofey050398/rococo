package timofeyqa.rococo.page.lists;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;

public interface ListPage<T extends ListPage<T>> {

  int pageSize();

  ElementsCollection cards();

  @Step("Scroll to next page and assert for next page loaded")
  @SuppressWarnings("unchecked")
  default T nextPageAndCompare() {
    cards().shouldHave(sizeGreaterThan(pageSize()-1));
    int prevSize = cards().size();
    Selenide.executeJavaScript(
        "arguments[0].scrollIntoView(true);",
        cards().last().toWebElement()
    );
    cards().shouldHave(sizeGreaterThan(prevSize));
    return (T) this;
  }

  @Step("Assert card size")
  @SuppressWarnings("unchecked")
  default T assertCardSize(int expectedSize) {
    cards().shouldHave(size(expectedSize));
    return (T) this;
  }

  public ListPage<?> compareCardNotFound();

  public ListPage<?> comparePageIsEmpty();
}
