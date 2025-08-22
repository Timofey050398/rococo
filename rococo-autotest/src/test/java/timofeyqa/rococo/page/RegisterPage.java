package timofeyqa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static timofeyqa.rococo.condition.ScreenshotCondition.image;

public class RegisterPage extends BasePage<RegisterPage>{

  private final SelenideElement successRegisterHeader = $("p.form__subheader");
  private final SelenideElement usernameInput = $("input#username");
  private final SelenideElement passwordInput = $("input#password");
  private final SelenideElement passwordSubmitInput = $("input#passwordSubmit");
  private final SelenideElement registerButton = $(byText("Зарегистрироваться"));
  private final SelenideElement submitButton = $(byText("Войти в систему"));
  private final SelenideElement loginLink = $("p.form__paragraph a.form__link");

  private final SelenideElement image = $("img.content__image");

  public static final String URL = CFG.authUrl()+"register";

  @Override
  public RegisterPage checkThatPageLoaded() {
    image.shouldBe(visible);
    usernameInput.shouldBe(visible);
    passwordInput.shouldBe(visible);
    return this;
  }

  @Step("Ввести имя пользователя: {username}")
  public RegisterPage setUsername(String username) {
    usernameInput.setValue(username);
    return this;
  }

  @Step("Ввести пароль: [скрыто]")
  public RegisterPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  @Step("Ввести подтверждение пароля: [скрыто]")
  public RegisterPage setPasswordSubmit(String passwordSubmit) {
    passwordSubmitInput.setValue(passwordSubmit);
    return this;
  }

  @Step("Отправить форму регистрации")
  public RegisterPage register() {
    registerButton.click();
    return this;
  }

  @Step("Register with data {username}, {password}, {passwordSubmit}")
  public RegisterPage submitRegister(String username, String password, String passwordSubmit){
    return setUsername(username)
        .setPassword(password)
        .setPasswordSubmit(passwordSubmit)
        .register();
  }

  @Step("Войти в систему")
  public LoginPage submit() {
    submitButton.click();
    return new LoginPage();
  }

  @Step("Перейти на страницу логина")
  public LoginPage goToLoginPage() {
    loginLink.click();
    return  new LoginPage();
  }

  @Step("Check that register page has expected image")
  public RegisterPage checkRenuarImage(BufferedImage expected){
    image.shouldHave(image(expected));
    return this;
  }

  @Step("Assert success register screen")
  public RegisterPage assertSuccessRegisterScreen(){
    successRegisterHeader.shouldHave(text("Добро пожаловать в Rococo"));
    submitButton.shouldBe(visible);
    return this;
  }
}

