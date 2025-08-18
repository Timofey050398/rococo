package timofeyqa.rococo.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import timofeyqa.rococo.config.Config;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

    public abstract T checkThatPageLoaded();
    protected static final Config CFG = Config.getInstance();
    private final ElementsCollection formErrors = $$("p.form__error, span.form__error, .input__helper-text");

    @Step("Check that form error message appears: {expectedText}")
    @SuppressWarnings("unchecked")
    @Nonnull
    public T checkFormErrorMessage(String... expectedText) {
        formErrors.should(CollectionCondition.textsInAnyOrder(expectedText));
        return (T) this;
    }
}
