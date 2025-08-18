package timofeyqa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

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
  private final SelenideElement avatarImage = self.$("img.avatar-image");
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
  public Header clickLoginButton() {
    loginButton.click();
    return this;
  }

  @Step("Check that avatar image is visible")
  public Header verifyAvatarVisible() {
    avatarImage.shouldBe(visible);
    return this;
  }

  public ProfileModal openProfile(){
    profileButton.shouldBe(visible)
        .click();
    return new ProfileModal();
  }
}
