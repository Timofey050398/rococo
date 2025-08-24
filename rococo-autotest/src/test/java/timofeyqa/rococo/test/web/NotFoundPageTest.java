package timofeyqa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.page.NotFoundPage;

@WebTest
@DisplayName("Тесты страницы 404")
public class NotFoundPageTest {

  @Test
  @DisplayName("Если пользователь ввел не существующий урл, пользователь должен попасть на страницу 404")
  void whenUserTypeWrongRoutUriThenRedirectToNotFoundPage(){
    Selenide.open(NotFoundPage.URL, NotFoundPage.class)
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("По нажатию пользователем кнопки 'На главную' на странице 404 пользователь попадает на главную")
  void whenUserClickMainPageTheRedirect(){
    Selenide.open(NotFoundPage.URL, NotFoundPage.class)
        .checkThatPageLoaded()
        .clickMainPageButton()
        .checkThatPageLoaded();
  }
}
