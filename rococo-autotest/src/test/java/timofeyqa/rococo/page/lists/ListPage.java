package timofeyqa.rococo.page.lists;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import timofeyqa.rococo.ex.BadPreConditionException;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.*;

@SuppressWarnings("UnusedReturnValue")
@ParametersAreNonnullByDefault
public interface ListPage<T extends ListPage<T>> {

  int pageSize();

  ElementsCollection list();

  @Step("Scroll to next page and assert for next page loaded")
  @SuppressWarnings("unchecked")
  default T nextPageAndCompare() {
    try {
      list().shouldHave(sizeGreaterThanOrEqual(pageSize()));
    } catch (AssertionError e) {
      throw new BadPreConditionException("To open next page, total cards should be greater than page size");
    }
    int prevSize = list().size();
    Selenide.executeJavaScript(
        "arguments[0].scrollIntoView(true);",
        list().last().toWebElement()
    );
    list().shouldHave(sizeGreaterThan(prevSize));
    return (T) this;
  }

  @Step("Assert card size")
  @SuppressWarnings("unchecked")
  default T assertCardSize(int expectedSize) {
    list().shouldHave(size(expectedSize));
    return (T) this;
  }
}
