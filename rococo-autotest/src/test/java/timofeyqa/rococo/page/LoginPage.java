package timofeyqa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static timofeyqa.rococo.condition.ScreenshotCondition.image;

public class LoginPage extends BasePage<LoginPage>{

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement passwordToggleButton = $(".form__password-button");
  private final SelenideElement submitButton = $(".form__submit");
  private final SelenideElement registerLink = $(".form__link");
  private final SelenideElement hermitageImage = $("img[alt='Эрмитаж']");

  public static final String URL = CFG.authUrl()+"login";

  @Override
  public LoginPage checkThatPageLoaded() {
    usernameInput.shouldBe(visible);
    passwordInput.shouldBe(visible);
    return this;
  }

  @Step("Set username: {username}")
  public LoginPage setUsername(String username) {
    usernameInput.setValue(username);
    return this;
  }

  @Step("Set password")
  public LoginPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  @Step("Toggle password visibility")
  public LoginPage togglePasswordVisibility() {
    passwordToggleButton.click();
    return this;
  }

  @Step("Submit login form")
  public void submit() {
    submitButton.click();
  }

  @Step("login by user 'username'")
  public MainPage login(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    submit();
    return new MainPage();
  }

  @Step("Click register link")
  public RegisterPage clickRegister() {
    registerLink.click();
    return new RegisterPage();
  }

  @Step("Check that login page has expected image")
  public LoginPage checkHermitageImage(BufferedImage expected){
    hermitageImage.shouldHave(image(expected));
    return this;
  }

}
