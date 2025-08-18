package timofeyqa.rococo.page.component;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class Title extends BaseComponent<Title> {
  public Title(){
    super($("h2.text-3xl.m-4"));
  }

  @Step("comapre that title has text {titleName}")
  public void compareText(String titleName){
    self.shouldHave(text(titleName));
  }
}
