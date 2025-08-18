package timofeyqa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import timofeyqa.rococo.jupiter.annotation.ApiLogin;
import timofeyqa.rococo.jupiter.annotation.ScreenShotTest;
import timofeyqa.rococo.jupiter.annotation.User;
import timofeyqa.rococo.jupiter.annotation.meta.WebTest;
import timofeyqa.rococo.page.MainPage;

import java.awt.image.BufferedImage;

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
}
