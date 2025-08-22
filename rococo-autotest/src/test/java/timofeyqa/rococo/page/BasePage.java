package timofeyqa.rococo.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import timofeyqa.rococo.config.Config;

import javax.annotation.ParametersAreNonnullByDefault;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

    public abstract T checkThatPageLoaded();
    protected static final Config CFG = Config.getInstance();
    private final ElementsCollection formErrors = $$("p.form__error, span.form__error, .input__helper-text");
    private final SelenideElement toast = $("div.toast");

    @Step("Check that form error message appears: {expectedText}")
    @SuppressWarnings("unchecked")
    @Nonnull
    public T checkFormErrorMessage(String... expectedText) {
        formErrors.should(CollectionCondition.textsInAnyOrder(expectedText));
        return (T) this;
    }

    @Step("Check that toast message appears: {expectedText}")
    @SuppressWarnings("unchecked")
    @Nonnull
    public T checkToastMessage(String expectedText) {
        toast.should(text(expectedText));
        return (T) this;
    }

    @Step("Check that toast was hidden")
    @SuppressWarnings("unchecked")
    @Nonnull
    public T checkToastHidden() {
        toast.should(not(visible), Duration.ofSeconds(7));
        return (T) this;
    }
}
