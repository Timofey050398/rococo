package timofeyqa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import timofeyqa.rococo.page.LoginPage;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static timofeyqa.rococo.condition.ScreenshotCondition.image;

public class Header extends BaseComponent<Header> {
  public Header(){
    super($("header[id='shell-header']"));
  }
  private final SelenideElement menuButton = self.$("button img[alt='Иконка меню']");
  private final SelenideElement paintingsLink = self.$("nav.list-nav a[href='/painting']");
  private final SelenideElement artistsLink = self.$("nav.list-nav a[href='/artist']");
  private final SelenideElement museumsLink = self.$("nav.list-nav a[href='/museum']");
  private final SelenideElement lightSwitch = self.$("div.lightswitch-track");
  private final SelenideElement loginButton = self.$("button.btn.variant-filled-primary");
  private final SelenideElement avatarImage = self.$("img.avatar-image, svg.avatar-initials");
  private final SelenideElement profileButton = self.$("figure").parent();

  @Step("Click menu button")
  public Header clickMenuButton() {
    menuButton.click();
    return this;
  }

  @Step("Click link 'Картины'")
  public Header clickPaintingsLink() {
    paintingsLink.click();
    return this;
  }

  @Step("Click link 'Художники'")
  public Header clickArtistsLink() {
    artistsLink.click();
    return this;
  }

  @Step("Click link 'Музеи'")
  public Header clickMuseumsLink() {
    museumsLink.click();
    return this;
  }

  @Step("Toggle light/dark mode switch")
  public Header toggleLightSwitch() {
    lightSwitch.click();
    return this;
  }

  @Step("Click login button")
  public LoginPage clickLoginButton() {
    loginButton
        .shouldBe(visible)
        .click();
    return new LoginPage();
  }

  @Step("Check that avatar image is visible")
  public Header verifyAvatarVisible() {
    avatarImage.shouldBe(visible);
    return this;
  }

  @Step("Check avatar image")
  public Header checkAvatarImage(BufferedImage expected) {
    avatarImage.shouldBe(image(expected));
    return this;
  }

  public ProfileModal openProfile(){
    profileButton.shouldBe(visible)
        .click();
    return new ProfileModal();
  }

  @Step("Assert that user authorized")
  public Header assertAuthorized(){
    profileButton.shouldBe(exist);
    return this;
  }
}
