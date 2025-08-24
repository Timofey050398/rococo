package timofeyqa.rococo.page.component.forms;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import timofeyqa.rococo.page.component.BaseComponent;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.URL;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static timofeyqa.rococo.condition.ValidationConditions.requiredFile;

public abstract class Form<T extends Form<?>> extends BaseComponent<T> {


  private final String imageParamName;

  protected SelenideElement imageInput = self.$("input[type='file']");
  protected SelenideElement closeModalButton = self.$("button[type='button']");
  protected SelenideElement submitButton = self.$("button[type='submit']");
  protected SelenideElement image = self.$("img");
  private final ElementsCollection formErrors = $$("p.form__error, span.form__error, .input__helper-text, .text-error-400");
  private final SelenideElement toast = $("div.toast");

  public Form(String imageParamName) {
    super($("div.card.p-4"));
    this.imageParamName = imageParamName;
  }

  public Form() {
    super($("div.card.p-4"));
    this.imageParamName = "photo";
  }

  @Step("Check that form error message appears: {expectedText}")
  @SuppressWarnings("unchecked")
  @Nonnull
  public T checkFormErrorMessage(String... expectedText) {
    formErrors
        .filter(not(empty))
        .should(CollectionCondition.textsInAnyOrder(expectedText));
    return (T) this;
  }

  @Step("Check that toast message appears: {expectedText}")
  @SuppressWarnings("unchecked")
  @Nonnull
  public T checkToastMessage(String expectedText) {
    toast.should(text(expectedText));
    return (T) this;
  }

  public T checkToastFileSizeErrorMessage() {
    final String expectedText = String.format("%s: File size exceeds allowed limit",imageParamName);
    return checkToastMessage(expectedText);
  }

  public abstract T checkThatPageLoaded();

  @SuppressWarnings("unchecked")
  @Step("upload image ti form from resources path {resourcePath}")
  public T uploadImage(String resourcePath) {
    URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
    if (resourceUrl == null) {
      throw new IllegalArgumentException("Resource not found: " + resourcePath);
    }
    File file = new File(resourceUrl.getFile());
    imageInput.uploadFile(file);
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  @Step("Click submit button")
  public T clickSubmitButton(){
    submitButton.shouldBe(visible).click();
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  @Step("Assert image required")
  public T assertImageRequired(){
    imageInput.shouldBe(requiredFile);
    return (T) this;
  }

  @Step("Close modal")
  public <B> B closeModal(Class<B> clazz){
    closeModalButton.shouldBe(visible).click();
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Cannot create instance of " + clazz, e);
    }
  }
}
