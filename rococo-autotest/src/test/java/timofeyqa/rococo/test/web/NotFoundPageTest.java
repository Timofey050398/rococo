package timofeyqa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.page.NotFoundPage;

@WebTest
public class NotFoundPageTest {

  @Test
  void whenUserTypeWrongRoutUriThenRedirectToNotFoundPage(){
    Selenide.open(NotFoundPage.URL, NotFoundPage.class)
        .checkThatPageLoaded();
  }

  @Test
  void whenUserClickMainPageTheRedirect(){
    Selenide.open(NotFoundPage.URL, NotFoundPage.class)
        .checkThatPageLoaded()
        .clickMainPageButton()
        .checkThatPageLoaded();
  }
}
