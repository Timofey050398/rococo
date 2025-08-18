package timofeyqa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import timofeyqa.rococo.page.BasePage;

import java.awt.image.BufferedImage;

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

  @Step("Get username label text")
  public String getUsername() {
    return usernameLabel.getText();
  }

  @Step("Upload profile photo from file {filePath}")
  public ProfileModal uploadProfilePhoto(String filePath) {
    updatePhotoInput.uploadFile(new java.io.File(filePath));
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
  public <T extends BasePage> T clickLogoutButton(T page) {
    logoutButton.click();
    return page;
  }

  @Step("Click 'Закрыть' button and return page instance")
  public <T extends BasePage> T clickCloseButton(T page) {
    closeButton.click();
    return page;
  }

  @Step("Click 'Обновить профиль' button and return page instance")
  public <T extends BasePage> T clickUpdateProfileButton(T page) {
    updateProfileButton.click();
    return page;
  }
}
