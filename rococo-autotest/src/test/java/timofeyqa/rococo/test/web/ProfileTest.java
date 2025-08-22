package timofeyqa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import timofeyqa.rococo.jupiter.annotation.ApiLogin;
import timofeyqa.rococo.jupiter.annotation.ScreenShotTest;
import timofeyqa.rococo.jupiter.annotation.User;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.page.MainPage;
import timofeyqa.rococo.page.component.Header;
import timofeyqa.rococo.utils.RandomDataUtils;

import java.awt.image.BufferedImage;

import static timofeyqa.rococo.utils.RandomDataUtils.randomFirstname;

@WebTest
public class ProfileTest {

  @ScreenShotTest("img/pages/profile/avatar-small.png")
  @User(avatar = "img/pages/profile/avatar.png")
  @ApiLogin
  void avatarShouldBeShown(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .checkAvatarImage(expected);
  }

  @ScreenShotTest("img/pages/profile/avatar-modal.png")
  @User(avatar = "img/pages/profile/avatar.png")
  @ApiLogin
  void avatarInModalShouldBeShown(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .openProfile()
        .checkAvatarImage(expected);
  }

  @ScreenShotTest("img/pages/profile/avatar-template-small.png")
  @User
  @ApiLogin
  void avatarTemplateShouldBeShown(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .checkAvatarImage(expected);
  }

  @ScreenShotTest("img/pages/profile/avatar-template.png")
  @User
  @ApiLogin
  void avatarTemplateInModalShouldBeShown(BufferedImage expected){
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .openProfile()
        .checkAvatarImage(expected);
  }

  @ScreenShotTest("img/pages/profile/avatar-small.png")
  @User
  @ApiLogin
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
