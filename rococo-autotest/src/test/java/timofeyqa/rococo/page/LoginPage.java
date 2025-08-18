package timofeyqa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage>{
  @Override
  public LoginPage checkThatPageLoaded() {
    return null;
  }
  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement passwordToggleButton = $(".form__password-button");
  private final SelenideElement submitButton = $(".form__submit");
  private final SelenideElement registerLink = $(".form__link");
  private final SelenideElement hermitageImage = $("img[alt='Эрмитаж']");

  public static final String URL = CFG.authUrl()+"/login";

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

}
