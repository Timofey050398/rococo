package timofeyqa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.ApiLogin;
import timofeyqa.rococo.jupiter.annotation.ScreenShotTest;
import timofeyqa.rococo.jupiter.annotation.User;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.page.MainPage;

import java.awt.image.BufferedImage;

import static timofeyqa.rococo.utils.RandomDataUtils.randomFirstname;

@WebTest
@DisplayName("Тесты компонента профиля")
public class ProfileTest {

  @ScreenShotTest("img/pages/profile/avatar-modal.png")
  @User(avatar = "img/pages/profile/avatar.png")
  @ApiLogin
  @DisplayName("У пользователя с аватаром аватар должен отображаться в модальном окне профиля")
  void avatarInModalShouldBeShown(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .openProfile()
        .checkAvatarImage(expected);
  }

  @ScreenShotTest("img/pages/profile/avatar-template.png")
  @User
  @ApiLogin
  @DisplayName("У пользователя без аватара должна отображатсья заглушка в модальном окне")
  void avatarTemplateInModalShouldBeShown(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .openProfile()
        .checkAvatarImage(expected);
  }

  @ScreenShotTest("img/pages/profile/avatar-small.png")
  @User
  @ApiLogin
  @DisplayName("У пользователя после обновления аватара должен отображаться новый аватар")
  void avatarShouldSettledAfterUpdate(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .openProfile()
        .uploadProfilePhoto("img/pages/profile/avatar.png")
        .clickUpdateProfileButton(MainPage.class)
        .checkToastMessage("Профиль обновлен")
        .checkToastHidden()
        .getHeader()
        .checkAvatarImage(expected);
  }

  @User(firstname = "Boris", lastname = "Borisov")
  @ApiLogin
  @Test
  @DisplayName("В профиле должны отображаться данные пользователя")
  void profileShouldShowUserData(UserJson user){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .openProfile()
        .compareUsername(user.username())
        .compareFirstname(user.firstname())
        .compareSurname(user.lastname());
  }

  @User
  @ApiLogin
  @Test
  @DisplayName("Пользователь должен иметь возможность обновить данные профиля")
  void profileShouldCanBeUpdatedData(){
    final String firstname = randomFirstname();
    final String surname = randomFirstname();
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .openProfile()
        .setFirstName(firstname)
        .setSurname(surname)
        .clickUpdateProfileButton(MainPage.class)
        .checkToastMessage("Профиль обновлен")
        .checkToastHidden()
        .getHeader()
        .openProfile()
        .compareFirstname(firstname)
        .compareSurname(surname);
  }

  @User
  @ApiLogin
  @Test
  @DisplayName("По нажатию иконки закрытия профиля профиль должен быть закрыт")
  void whenClickCLoseButtonProfileShouldBeClosed(){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .openProfile()
        .clickCloseButton()
        .compareModalClosed();
  }

  @User
  @ApiLogin
  @Test
  @DisplayName("Пользователь должен иметь возможность логаута")
  void userShouldCanLogout(){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .openProfile()
        .clickLogoutButton()
        .checkToastMessage("Сессия завершена")
        .getHeader()
        .assertUnauthorized();
  }
}
