package timofeyqa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import timofeyqa.rococo.page.LoginPage;
import timofeyqa.rococo.page.MainPage;
import timofeyqa.rococo.page.lists.ArtistPage;
import timofeyqa.rococo.page.lists.MuseumPage;
import timofeyqa.rococo.page.lists.PaintingsPage;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static timofeyqa.rococo.condition.ScreenshotCondition.image;

public class Header extends BaseComponent<Header> {
  public Header(){
    super($("header[id='shell-header']"));
  }
  private final SelenideElement paintingsLink = self.$("nav.list-nav a[href='/painting']");
  private final SelenideElement artistsLink = self.$("nav.list-nav a[href='/artist']");
  private final SelenideElement museumsLink = self.$("nav.list-nav a[href='/museum']");
  private final SelenideElement lightSwitch = self.$("div.lightswitch-track");
  private final SelenideElement loginButton = self.$("button.btn.variant-filled-primary");
  private final SelenideElement avatarImage = self.$("img.avatar-image, svg.avatar-initials");
  private final SelenideElement logo = self.$("h1 a");
  private final SelenideElement profileButton = self.$("figure").parent();

  @Step("Click logo button")
  public MainPage clickLogo() {
    logo.click();
    return new MainPage();
  }

  @Step("Click link 'Картины'")
  public PaintingsPage clickPaintingsLink() {
    paintingsLink.click();
    return new PaintingsPage();
  }

  @Step("Click link 'Художники'")
  public ArtistPage clickArtistsLink() {
    artistsLink.click();
    return new ArtistPage();
  }

  @Step("Click link 'Музеи'")
  public MuseumPage clickMuseumsLink() {
    museumsLink.click();
    return new MuseumPage();
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

  @Step("Assert that user unauthorized")
  public Header assertUnauthorized(){
    profileButton.shouldBe(not(visible));
    loginButton.shouldBe(visible);
    return this;
  }
}
