package timofeyqa.rococo.condition;

import com.codeborne.selenide.*;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ValidationConditions {

  private static final boolean IS_SELENOID = "Y".equals(System.getenv("SELENOID"));

  public static WebElementCondition validationMessage(String expectedMessage) {
    return new WebElementCondition("validation message") {

      @NotNull
      @Override
      public CheckResult check(Driver driver, WebElement element) {
        String actualMessage = element.getDomProperty("validationMessage");
        return new CheckResult(
            expectedMessage.equals(actualMessage),
            String.format("Expected: \"%s\", Actual: \"%s\"", expectedMessage, actualMessage)
        );
      }
    };
  }

  public static final WebElementCondition requiredInput = validationMessage(IS_SELENOID
      ? "Please fill out this field."
      : "Заполните это поле."
  );
  public static final WebElementCondition requiredFile = validationMessage(IS_SELENOID
      ? "Please select a file."
      : "Выберите файл."
  );
  public static final WebElementCondition requiredList = validationMessage(IS_SELENOID
      ? "Please select an item in the list."
      : "Выберите один из пунктов списка."
  );
}
