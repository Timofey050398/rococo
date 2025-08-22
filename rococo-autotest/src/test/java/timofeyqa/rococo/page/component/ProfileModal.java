package timofeyqa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import timofeyqa.rococo.page.MainPage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static timofeyqa.rococo.condition.ScreenshotCondition.image;

public class ProfileModal extends BaseComponent<ProfileModal> {

  private final SelenideElement avatarImage = self.$("figure.avatar img.avatar-image, svg.avatar-initials");
  private final SelenideElement usernameLabel = self.$("h4.text-center");
  private final SelenideElement updatePhotoInput = self.$("input[name='content']");
  private final SelenideElement firstNameInput = self.$("input[name='firstname']");
  private final SelenideElement surnameInput = self.$("input[name='surname']");
  private final SelenideElement logoutButton = self.$("button.btn.variant-ghost");
  private final SelenideElement closeButton = self.$("button.btn.variant-ringed");
  private final SelenideElement updateProfileButton = self.$("button.btn.variant-filled-primary");

  public ProfileModal() {
    super($("div.card.p-4.w-modal.shadow-xl.space-y-4"));
  }

  @Step("Check avatar image element")
  public ProfileModal checkAvatarImage(BufferedImage expected) {
    avatarImage.shouldBe(image(expected));
    return this;
  }

  @Step("Upload profile photo from file {filePath}")
  public ProfileModal uploadProfilePhoto(String filePath) {
    URL resource = getClass().getClassLoader().getResource(filePath);
    if (resource == null) {
      throw new IllegalArgumentException("File not found in resources: " + filePath);
    }
    File file = new File(resource.getFile());
    updatePhotoInput.uploadFile(file);
    return this;
  }

  @Step("Set first name to '{firstName}'")
  public ProfileModal setFirstName(String firstName) {
    firstNameInput.setValue(firstName);
    return this;
  }

  @Step("Set surname to '{surname}'")
  public ProfileModal setSurname(String surname) {
    surnameInput.setValue(surname);
    return this;
  }

  @Step("Click 'Выйти' button and return page instance")
  public MainPage clickLogoutButton() {
    logoutButton.click();
    return new MainPage();
  }

  @Step("Click 'Закрыть' button and return page instance")
  public ProfileModal clickCloseButton() {
    closeButton.click();
    return this;
  }

  public MainPage compareModalClosed(){
    self.shouldBe(not(visible));
    return new MainPage();
  }

  @Step("Compare username")
  public ProfileModal compareUsername(String username) {
    usernameLabel.shouldHave(text("@"+username));
    return this;
  }

  @Step("Compare firstname")
  public ProfileModal compareFirstname(String firstname) {
    firstNameInput.shouldHave(attribute("value",firstname));
    return this;
  }

  @Step("Compare surname")
  public ProfileModal compareSurname(String surname) {
    surnameInput.shouldHave(attribute("value",surname));
    return this;
  }

  @Step("Click 'Обновить профиль' button and return page instance")
  public <T> T clickUpdateProfileButton(Class<T> page) {
    updateProfileButton.click();
    try {
      return page.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Cannot create instance of " + page, e);
    }
  }
}
