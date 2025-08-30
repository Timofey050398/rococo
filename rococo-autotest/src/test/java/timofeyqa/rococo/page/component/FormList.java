package timofeyqa.rococo.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import timofeyqa.rococo.page.lists.ListPage;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

@ParametersAreNonnullByDefault
public class FormList extends BaseComponent<FormList> implements ListPage<FormList> {

  private final int pageSize;

  public FormList(SelenideElement parent, String classValue, int pageSize){
    super(parent.$("select[name='"+classValue+"']"));
    this.pageSize = pageSize;
  }

  @Override
  public int pageSize() {
    return pageSize;
  }

  @Override
  public ElementsCollection list() {
    return self.$$("option");
  }

  @Step("search list value {search}")
  public SelenideElement search(String search) {
    list().should(sizeGreaterThan(0));
    while (!list().isEmpty()) {
      if (list().stream().anyMatch(el -> el.is(text(search)))) {
        return list().findBy(text(search))
            .shouldBe(visible);
      } else {
        String lastElement = list().last().getText();
        if (search.compareToIgnoreCase(lastElement) < 0) {
          String error = String.format("Search result not found: lastElement= %s, search= %s",lastElement,search);
          throw new AssertionError(error);
        }
        nextPageAndCompare();
      }
    }
    throw new AssertionError("List is empty, search aborted");
  }
}
