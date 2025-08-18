package timofeyqa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage extends BasePage<RegisterPage>{

  private final SelenideElement usernameInput = $("input#username");
  private final SelenideElement passwordInput = $("input#password");
  private final SelenideElement passwordSubmitInput = $("input#passwordSubmit");
  private final SelenideElement registerButton = $(byText("Зарегистрироваться"));
  private final SelenideElement submitButton = $(byText("Войти в систему"));
  private final SelenideElement loginLink = $("p.form__paragraph a.form__link");

  private final SelenideElement image = $("img.content__image");

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

  @Step("Проверить отображение картинки регистрации")
  public boolean isImageVisible() {
    return image.isDisplayed();
  }
}

