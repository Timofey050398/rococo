package timofeyqa.rococo.condition;

import com.codeborne.selenide.*;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ValidationConditions {

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

  public static final WebElementCondition requiredInput = validationMessage("Заполните это поле.");
  public static final WebElementCondition requiredFile = validationMessage("Выберите файл.");
  public static final WebElementCondition requiredList = validationMessage("Выберите один из пунктов списка.");
}
